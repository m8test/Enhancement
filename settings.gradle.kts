pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            val m8testVersion: String by settings
            url = uri("https://raw.githubusercontent.com/m8test/Maven/refs/heads/${m8testVersion}/")
        }
    }
}

val m8testVersion: String by settings
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        // jcenter()
        maven {
            url = uri("https://raw.githubusercontent.com/m8test/Maven/refs/heads/${m8testVersion}/")
        }
    }
    versionCatalogs {
        create("m8test") {
            from("com.m8test:version-catalog:${m8testVersion}")
        }
    }
}

rootProject.name = "Enhancement"
include(":enhancement")