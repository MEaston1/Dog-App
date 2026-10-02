import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

/**
 * Writes the API key into a generated Kotlin source file rather than BuildConfig, which AGP
 * generates for Android only. The output feeds commonMain, so every platform reads the key
 * by the same route.
 */
abstract class GenerateApiKey : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val apiKey: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val key = apiKey.orNull?.takeIf { it.isNotBlank() } ?: error(
            "DOG_API_KEY is missing from local.properties. " +
                "Copy local.properties.example and add a key from https://thedogapi.com."
        )
        val escaped = key.replace("\\", "\\\\").replace("\"", "\\\"")
        outputDir.get().file("com/measton/dogapp/ApiKey.kt").asFile.apply {
            parentFile.mkdirs()
            writeText(
                """
                package com.measton.dogapp

                internal const val DOG_API_KEY = "$escaped"
                """.trimIndent() + "\n"
            )
        }
    }
}

val generateApiKey = tasks.register<GenerateApiKey>("generateApiKey") {
    apiKey.set(localProperties.getProperty("DOG_API_KEY"))
    outputDir.set(layout.buildDirectory.dir("generated/apikey"))
}

kotlin {
    jvmToolchain(17)

    android {
        // Must differ from :androidApp's namespace, which owns com.measton.dogapp.
        namespace = "com.measton.dogapp.shared"
        compileSdk = 37
        minSdk = 24

        // Off by default in a KMP library; Compose Multiplatform resources need it on Android.
        androidResources {
            enable = true
        }

        withHostTest {}
    }

    // Declared so commonMain is checked against a non-JVM target. These cannot be built on
    // Windows - Kotlin/Native has no Apple toolchain here - so they are CI-only (Phase 8).
    // iosX64 is deliberately absent: neither androidx.lifecycle nor its JetBrains fork
    // publishes an ios_x64 variant, and declaring the target strips androidx.lifecycle out
    // of commonMain's shared API surface (unresolved ViewModel in every ViewModel class).
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateApiKey)
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.bundles.ktor)
                implementation(libs.koin.core)
                implementation(libs.lifecycle.viewmodel)
                implementation(libs.koin.core.viewmodel)

                // Compose Multiplatform - shared UI (theme, screens, components) lives here.
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Multiplatform Koin-Compose integration (replaces koin-androidx-compose).
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // Coil 3 image loading with a Ktor-backed network fetcher (per-platform engine).
                implementation(libs.bundles.remoteImages)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
                implementation(libs.ktor.client.mock)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                // Supplies Dispatchers.Main, which viewModelScope runs on.
                implementation(libs.coroutines)
                // api: :androidApp's MainActivity creates the NavController it passes to
                // NavGraph/BottomNavigation. Goes away with Navigation 3 (Phase 7).
                api(libs.navigation.compose)
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(libs.koin.test)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

compose.resources {
    packageOfResClass = "com.measton.dogapp.resources"
}

dependencies {
    // A KMP library has no debug variant, so preview tooling goes on the Android runtime classpath.
    "androidRuntimeClasspath"(compose.uiTooling)
}
