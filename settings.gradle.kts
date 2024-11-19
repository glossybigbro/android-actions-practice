pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.gradle.develocity") version("3.18.2")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.develocity")
}

val isCI = !System.getenv("CI").isNullOrEmpty()

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        uploadInBackground.set(!isCI)
        capture.fileFingerprints.set(true)
    }
}

buildCache {
    local {
        isEnabled = !isCI
        removeUnusedEntriesAfterDays = 30
    }
}

rootProject.name = "android-actions-practice"
include(":app")
 