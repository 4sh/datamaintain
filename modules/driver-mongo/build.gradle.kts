plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
    maven // Needed for Jitpack
}

baseProject()

repositories {
    jcenter()
}

dependencies {
    implementation(project(":modules:core"))
    implementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")
}

mongo {
    setPort(Globals.mongoPort)
    mongoVersion = Versions.mongo
}


tasks.getByPath("test").dependsOn("startManagedMongoDb")
