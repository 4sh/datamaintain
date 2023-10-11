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
    implementation(project(":modules:driver-mongo"))
    implementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-serialization"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")
    implementation("org.mongodb:mongodb-driver:3.11.2")
    implementation("org.jongo:jongo:${Versions.jongo}")
}
