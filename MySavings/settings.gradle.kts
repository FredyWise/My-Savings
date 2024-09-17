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
include(":theme")
include(":domain")
include(":data")
include(":features:preferences")
include(":features:wallet")
include(":features:currency")
include(":features:category")
include(":features:io")
include(":features:search")
include(":features:addRecord")
include(":features:analysis")
include(":ui")
include(":features:book")
