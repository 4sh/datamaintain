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
