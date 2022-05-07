plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

javafx {
    version = "17"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml"
    )
}

repositories {
    mavenLocal()
    mavenCentral()
}

application {
    mainClass.set("com.mythicalcreaturesoftware.splash.Main" as String?)
}

dependencies {
    implementation("de.saxsys:mvvmfx:1.8.0")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.controlsfx:controlsfx:11.1.1")
    implementation("de.jensd:fontawesomefx-fontawesome:4.7.0-9.1.2")
    implementation("de.jensd:fontawesomefx-materialdesignfont:2.0.26-9.1.2")
    implementation("de.jensd:fontawesomefx-materialicons:2.2.0-9.1.2")
    implementation("de.jensd:fontawesomefx-materialstackicons:2.1-5-9.1.2")
    implementation("com.github.goxr3plus:FX-BorderlessScene:4.4.0")
    implementation(project(":reader"))

    annotationProcessor("de.saxsys:mvvmfx:1.8.0")

    testImplementation("junit:junit:4.13.2")
}