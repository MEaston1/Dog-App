pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "Dog App"
include(":app")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}