plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version Versions.kotlin
    `maven-publish` // Needed for Jitpack
    id("com.adarshr.test-logger")
}

baseProject()

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "datamaintain-" + project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}

dependencies {
    compileOnly(project(":modules:core"))
    testImplementation(project(":modules:core"))
    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
    )
    testImplementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")
}
