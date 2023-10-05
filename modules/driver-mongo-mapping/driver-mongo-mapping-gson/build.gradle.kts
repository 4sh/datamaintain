plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish` // Needed for Jitpack
}

baseProject()

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
