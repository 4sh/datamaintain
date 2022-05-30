plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version Versions.kotlin
    `maven-publish` // Needed for Jitpack
    id("com.adarshr.test-logger")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":modules:core"))
    compileOnly(project(":modules:driver-mongo"))
    compileOnly("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")

    implementation("com.jayway.jsonpath:json-path:2.7.0")
}
