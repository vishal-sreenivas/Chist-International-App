pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("agp", "8.2.2")
            version("kotlin", "1.9.22")
            
            plugin("android-application", "com.android.application").versionRef("agp")
            plugin("android-library", "com.android.library").versionRef("agp")
            plugin("kotlin-android", "org.jetbrains.kotlin.android").versionRef("kotlin")
        }
    }
}

rootProject.name = "Christ International"
include(":app")
 