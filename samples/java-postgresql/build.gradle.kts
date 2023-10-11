plugins {
    id("org.jetbrains.kotlin.jvm")
}

baseProject()

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-jdbc"))
    implementation("org.postgresql:postgresql:${Versions.postgresql}")
}
