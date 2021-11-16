plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish` // Needed for Jitpack
}

baseProject()

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-jdbc"))
    implementation("org.postgresql:postgresql:${Versions.postgresql}")
}
