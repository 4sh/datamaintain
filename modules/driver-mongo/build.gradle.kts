plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
}

baseProject()

repositories {
    jcenter()
}

dependencies {
    implementation(project(":modules:core"))

    implementation("org.litote.kmongo:kmongo:${Versions.kmongo}")

}

mongo {
    setPort(Globals.mongoPort)
    mongoVersion = Versions.mongo
}


tasks.getByPath("test").dependsOn("startManagedMongoDb")
