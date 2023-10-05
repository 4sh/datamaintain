plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version Versions.kotlin
    `maven-publish` // Needed for Jitpack
}

baseProject()

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":modules:core"))
    compileOnly(project(":modules:driver-mongo"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")

    testImplementation(project(":modules:core"))
    testImplementation(project(":modules:driver-mongo"))
    testImplementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")
}
