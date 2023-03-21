plugins {
    id("org.jetbrains.kotlin.jvm")
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

    testImplementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")

    // Use kotlinx-serialization implementation
    testImplementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-serialization"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")
    testImplementation("org.testcontainers:testcontainers:${Versions.testcontainers}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    testImplementation("org.testcontainers:mongodb:${Versions.testcontainers}")
}
