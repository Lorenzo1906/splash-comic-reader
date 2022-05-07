plugins {
    kotlin("multiplatform") version "1.7.0-Beta"
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("io.github.aakira:napier:1.4.1")
            }
        }

        val jvmMain by getting {
            dependencies {

                implementation("io.github.aakira:napier:1.4.1")
                implementation("commons-io:commons-io:2.6")
                implementation("com.github.axet:java-unrar:1.7.0-1")
                implementation("commons-io:commons-io:2.6")
                implementation("org.apache.commons:commons-lang3:3.10")
            }
        }
    }
}