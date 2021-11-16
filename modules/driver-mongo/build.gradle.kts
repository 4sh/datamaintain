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
    testImplementation(project(":modules:core"))
    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
    )
    testImplementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")
}
