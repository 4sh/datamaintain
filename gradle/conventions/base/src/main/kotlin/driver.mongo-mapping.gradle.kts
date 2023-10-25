package datamaintain.conventions

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the

// Access the version catalog inside the plugin
// See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm") // necessary to declare dependencies
}

dependencies {
    val mongo = project(":modules:driver-mongo")

    compileOnly(mongo)
    testImplementation(mongo)
    testImplementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-test"))

    testImplementation(libs.testContainers)
    testImplementation(libs.testContainers.jupiter)
    testImplementation(libs.testContainers.mongodb)
}
