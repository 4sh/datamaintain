package datamaintain.conventions

plugins {
    kotlin("jvm") // necessary to declare dependencies
}

dependencies {
    val core = project(":modules:core")

    compileOnly(core)
    testImplementation(core)
}
