plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
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
    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinxSerializationRuntime}"
    )
    testImplementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")
}

mongo {
    setPort(Globals.mongoPort)
    mongoVersion = Versions.mongo
}

tasks.getByPath("test").dependsOn("startManagedMongoDb")
