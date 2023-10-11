plugins {
    id("org.jetbrains.kotlin.jvm")
}

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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("org.mongodb:mongodb-driver:3.11.2")
    implementation("org.jongo:jongo:1.4.1")
}
