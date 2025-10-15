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

    // Pin plugin versions here so module build files can stay clean.
    plugins {
        id("com.android.application") version "8.13.0"
        id("com.android.library") version "8.13.0"

        // Kotlin (K2) + Compose plugin must match
        id("org.jetbrains.kotlin.android") version "2.2.20"
        id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"

        // KSP aligned to Kotlin
        id("com.google.devtools.ksp") version "2.2.20-1.0.29"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    // (Optional) Centralize library versions if you prefer TOML.
    // versionCatalogs {
    //     create("libs") {
    //         version("composeBom", "2025.10.00")
    //         version("nav", "2.9.5")
    //         version("lifecycle", "2.9.4")
    //         version("coroutines", "1.10.2")
    //         version("retrofitBom", "3.0.0")
    //         version("okhttp", "5.2.1")
    //         version("serializationJson", "1.9.0")
    //         version("room", "2.8.2")
    //         version("work", "2.10.5")
    //         version("coil3", "3.3.0")
    //         version("koin", "4.1.1")
    //     }
    // }
}

rootProject.name = "Ashad_Swipe"
include(":app")
