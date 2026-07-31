import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.kotlin)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    compileSdk = 37

    defaultConfig {
        applicationId = "com.measton.dogapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "DOG_API_KEY",
            "\"${localProperties.getProperty("DOG_API_KEY") ?: ""}\"",
        )
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

    testOptions {
        unitTests.all { unitTest ->
            unitTest.useJUnitPlatform()  // explicitly use JUnit 5
        }
    }
    namespace = "com.measton.dogapp"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.bundles.ui)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.remoteImages)
    implementation(libs.dagger.hilt)
    implementation(libs.androidx.ui.test.junit4.android)
    testImplementation(libs.androidx.junit)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.bundles.network)
    ksp(libs.moshi.kotlin.codegen)
    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.junit.jupiter.engine)
    androidTestImplementation(libs.bundles.androidTest)
    kaptAndroidTest(libs.dagger.hilt.android.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose.ui)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.manifest)
    androidTestImplementation(libs.compose.junit4)
}
