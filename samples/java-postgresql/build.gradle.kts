plugins {
    id("org.jetbrains.kotlin.jvm")
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
