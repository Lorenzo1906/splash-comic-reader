plugins {
    java
    id("org.panteleyev.jpackageplugin") version "1.3.1"
}

group = "com.mythicalcreaturesoftware"
version = "0.1.2"

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

tasks.register<GradleBuild>("buildAll") {
    tasks = listOf(":reader:build", ":app:build")
}

tasks.jpackage {
    dependsOn("buildAll")

    input  = "$projectDir/app/build/libs"
    destination = "$projectDir/app/build/tmp"
    verbose = true

    vendor = "Mythical Creature Software"
    appDescription = "Application to read .cbr and .cbz files"
    copyright = "Copyright 2020, All rights reserved"
    version = "$version"

    mainJar = "$projectDir/app/build/libs/app-all.jar"
    mainClass = "com.mythicalcreaturesoftware.splash.Main"

    windows {
        appName = "SplashComicReader"
        icon = "$projectDir/generalResources/icon.ico"
        winShortcut = true
        winDirChooser = true
        winMenu = true
        winMenuGroup = "Mythical Creature Software"
        type = org.panteleyev.jpackage.ImageType.MSI
        fileAssociations = listOf(
            "$projectDir/generalResources/cbrFile.properties",
            "$projectDir/generalResources/cbzFile.properties",
        )
    }

    linux {
        appName = "scr.AppImage"
        icon = "$projectDir/generalResources/icon.png"
        type = org.panteleyev.jpackage.ImageType.APP_IMAGE
    }

    mac {
        appName = "SplashComicReader"
        macPackageName = "splash-comic-reader"
        icon = "$projectDir/generalResources/icon.icns"
        fileAssociations = listOf(
            "$projectDir/generalResources/cbrFile.properties",
            "$projectDir/generalResources/cbzFile.properties",
        )
    }
}