import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
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

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
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
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(project.dependencies.platform(libs.compose.bom))
                implementation(libs.bundles.compose.ui)
                implementation(libs.bundles.ui)
                implementation(libs.bundles.lifecycle)
                implementation(libs.bundles.remoteImages)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.koin.test)
            }
        }

        androidInstrumentedTest {
            dependencies {
                implementation(libs.bundles.androidTest)
                implementation(libs.compose.junit4)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "com.measton.dogapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.measton.dogapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.manifest)
}
