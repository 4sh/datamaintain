plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish` // Needed for Jitpack
    id("com.adarshr.test-logger")
}

dependencies {
    "api"((this.platform("org.jetbrains.kotlin:kotlin-bom:${Versions.kotlin}")))
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

dependencies {
    "implementation"("io.github.microutils:kotlin-logging:${Versions.kotlinLogging}")

    "testImplementation"("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    "testImplementation"("org.junit.jupiter:junit-jupiter-params:${Versions.junit}")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
    "testImplementation"("io.strikt:strikt-core:${Versions.strikt}")
    "testImplementation"("io.mockk:mockk:${Versions.mockk}")
    "testImplementation"("org.testcontainers:testcontainers:${Versions.testcontainers}")
    "testImplementation"("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    "testImplementation"("org.testcontainers:mongodb:${Versions.testcontainers}")
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
    compileOnly(project(":modules:core"))
    compileOnly(project(":modules:driver-mongo"))
    compileOnly("com.google.code.gson:gson:${Versions.versionLatest}")

    testImplementation(project(":modules:core"))
    testImplementation(project(":modules:driver-mongo"))
    testImplementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-test"))
    testImplementation("com.google.code.gson:gson:${Versions.versionLatest}")
}
