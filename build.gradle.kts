import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin) apply false
    alias(libs.plugins.android.hilt) apply false
    id("com.github.ben-manes.versions") version "0.51.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks{
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }

    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            candidate.version.isNonStable()
        }
    }
}

subprojects {
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

fun String.isNonStable() = "^[0-9,.v-]+(-r)?$".toRegex().matches(this).not()
