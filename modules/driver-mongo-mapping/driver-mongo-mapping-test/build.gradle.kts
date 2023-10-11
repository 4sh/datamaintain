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
    compileOnly(projects.modules.core)
    compileOnly(projects.modules.driverMongo)
    compileOnly(libs.junit.jupiter.api)

    implementation(libs.jsonPath)
}
