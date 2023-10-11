plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.testLogger)
}

tasks.getByPath("test").doFirst({
    with<org.gradle.api.tasks.testing.Test, kotlin.Unit>(this as Test) {
        this.useJUnitPlatform()
    }
})

kotlin {
    jvmToolchain(17)
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

    implementation(project(":modules:cli"))
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation(libs.mongoDriver.sync)
}
