plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
    kotlin("plugin.serialization") version "1.3.70"
    id("maven-publish")
}

baseProject()

repositories {
    jcenter()
}

dependencies {
    implementation(project(":modules:core"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    testImplementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")
}

mongo {
    setPort(Globals.mongoPort)
    mongoVersion = Versions.mongo
}

tasks.getByPath("test").dependsOn("startManagedMongoDb")

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "driver-mongo"
            version = version
        }
    }
}