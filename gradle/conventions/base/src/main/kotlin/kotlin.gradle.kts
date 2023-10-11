package datamaintain.conventions

import org.gradle.accessors.dm.LibrariesForLibs

// Access the version catalog inside the plugin
// See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")
    id("com.palantir.git-version")
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

tasks.test {
    useJUnitPlatform()
}
