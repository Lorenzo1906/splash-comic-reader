rootProject.name = "splash-comic-reader"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

include(":app")
include(":reader")

enableFeaturePreview("VERSION_CATALOGS")

