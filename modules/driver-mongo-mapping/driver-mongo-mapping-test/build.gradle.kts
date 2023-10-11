plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":modules:core"))
    compileOnly(project(":modules:driver-mongo"))
    compileOnly(libs.junit.jupiter.api)

    implementation(libs.jsonPath)
}
