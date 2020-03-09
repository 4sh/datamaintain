plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
}

baseProject()

dependencies {
    implementation(project(":modules:cli"))
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")
}

mongo {
    setPort(Globals.mongoPort)
    mongoVersion = Versions.mongo
}

tasks.getByPath("test").dependsOn("startManagedMongoDb")
