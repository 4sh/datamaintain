plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version "1.3.70"
    maven // Needed for Jitpack
}

baseProject()

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":modules:core"))
    testImplementation(project(":modules:core"))
    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinxSerializationRuntime}"
    )
    testImplementation("com.h2database:h2:${Versions.h2Database}")
}
