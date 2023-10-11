plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}

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
    compileOnly(projects.modules.core)
    testImplementation(projects.modules.core)

    testImplementation(libs.mongoDriver.sync)

    // Use kotlinx-serialization implementation
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingSerialization)
    testImplementation(libs.kotlinx.serialization.json)
}
