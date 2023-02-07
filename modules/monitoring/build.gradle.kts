
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
    implementation(project(":modules:domain-report"))
    implementation("io.github.4sh.datamaintain-monitoring:api-execution-report:DEV")
    implementation("org.http4k:http4k-core:4.3.4.1")
    implementation("org.http4k:http4k-format-jackson:4.3.4.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.2")
}

