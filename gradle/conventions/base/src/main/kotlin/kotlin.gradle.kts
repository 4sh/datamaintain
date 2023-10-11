package datamaintain.conventions

import org.gradle.accessors.dm.LibrariesForLibs

// Access the version catalog inside the plugin
// See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")
    id("com.palantir.git-version")

    id("com.adarshr.test-logger")
}

group = "io.github.4sh.datamaintain"

// region Version generation

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val lastTag: String = versionDetails().lastTag

version = if (lastTag != "") {
    lastTag
} else {
    "SNAPSHOT"
}

// endregion

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    api(platform(libs.kotlin.bom))

    implementation(libs.kotlinLogging)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.strikt)
    testImplementation(libs.mockk)
    testImplementation(libs.testContainers)
    testImplementation(libs.testContainers.jupiter)
    testImplementation(libs.testContainers.mongodb)

    testImplementation(libs.logbackClassic)
}

tasks.test {
    useJUnitPlatform()
}
