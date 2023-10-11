plugins {
    alias(libs.plugins.kotlin.jvm)
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

    compileOnly(project(":modules:core"))
    compileOnly(project(":modules:driver-mongo"))
    compileOnly(libs.gson)

    testImplementation(project(":modules:core"))
    testImplementation(project(":modules:driver-mongo"))
    testImplementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-test"))
    testImplementation(libs.gson)
}
