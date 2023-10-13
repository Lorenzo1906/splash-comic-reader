plugins {
    kotlin("multiplatform") version "1.9.0"
}

group = "com.mythicalcreaturesoftware"
version = "0.1.2"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("io.github.oshai:kotlin-logging:5.1.0")
            }
        }

        val jvmMain by getting {
            dependencies {

                implementation("org.apache.lucene:lucene-queryparser:9.4.1")
                implementation("org.apache.lucene:lucene-core:9.4.1")
                implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
                implementation("commons-io:commons-io:2.6")
                implementation("com.github.axet:java-unrar:1.7.0-1")
                implementation("commons-io:commons-io:2.6")
                implementation("org.apache.commons:commons-lang3:3.10")
            }
        }
    }
}