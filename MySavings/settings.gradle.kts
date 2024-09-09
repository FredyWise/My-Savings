pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "MySavings"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":features:auth")
include(":core")
include(":theme")
include(":domain")
include(":data")
include(":features:preferences")
