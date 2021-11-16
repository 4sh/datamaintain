plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
    kotlin("plugin.serialization") version Versions.kotlin
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
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
    )
    testImplementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")
}

mongo {
    setPort(Globals.mongoPort)
    mongoVersion = Versions.mongo
}

tasks.getByPath("test").dependsOn("startManagedMongoDb")
