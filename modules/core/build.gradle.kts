
plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish` // Needed for Jitpack
    id("com.adarshr.test-logger")
}

repositories {
    mavenCentral()
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
    api(project(":modules:domain-report"))

    "testImplementation"("ch.qos.logback:logback-classic:${Versions.logbackClassic}")
}

