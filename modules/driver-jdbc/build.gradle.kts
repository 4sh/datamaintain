plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version "1.3.70"
    maven // Needed for Jitpack
}

baseProject()

repositories {
    jcenter()
}

dependencies {
    compileOnly(project(":modules:core"))
    testImplementation(project(":modules:core"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    implementation("org.postgresql:postgresql:42.2.14")
}
