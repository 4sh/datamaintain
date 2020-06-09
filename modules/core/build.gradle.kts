
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

repositories {
    jcenter()
}

baseProject()

dependencies {
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "core"
            version = version
        }
    }
}