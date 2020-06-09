plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
    id("maven-publish")
}

baseProject()

repositories {
    jcenter()
}

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation("org.mongodb:mongodb-driver:${Versions.mongoDriver}")
    implementation("org.jongo:jongo:1.4.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "sample-java-mongo"
            version = version
        }
    }
}