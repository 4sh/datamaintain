plugins {
    id("org.jetbrains.kotlin.jvm")
}

baseProject()

dependencies {
    implementation(project(":modules:cli"))
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation("org.mongodb:mongodb-driver-sync:${Versions.mongoDriver}")

    testImplementation("com.github.ajalt:clikt:${Versions.clikt}")
}
