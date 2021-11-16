plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
    maven // Needed for Jitpack
}

baseProject()

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation("org.mongodb:mongodb-driver:3.11.2")
    implementation("org.jongo:jongo:${Versions.jongo}")
}
