plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}

tasks.getByPath("test").doFirst({
    with<org.gradle.api.tasks.testing.Test, kotlin.Unit>(this as Test) {
        this.useJUnitPlatform()
    }
})

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    api(platform(libs.kotlin.bom))
    implementation(libs.kotlin.jdk8)

    implementation(libs.kotlinLogging)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.strikt)
    testImplementation(libs.mockk)
    testImplementation(libs.testContainers)
    testImplementation(libs.testContainers.jupiter)
    testImplementation(libs.testContainers.mongodb)

    compileOnly(projects.modules.core)
    compileOnly(projects.modules.driverMongo)
    compileOnly(libs.kotlinx.serialization.json)

    testImplementation(projects.modules.core)
    testImplementation(projects.modules.driverMongo)
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingTest)
    testImplementation(libs.kotlinx.serialization.json)
}
