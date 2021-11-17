plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version "1.3.70"
    `maven-publish` // Needed for Jitpack
    id("com.adarshr.test-logger")
}

baseProject()

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":modules:core"))
    testImplementation(project(":modules:core"))
    testImplementation("com.h2database:h2:${Versions.h2Database}")
}
