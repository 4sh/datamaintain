plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}

dependencies {
    compileOnly(projects.modules.core)
    compileOnly(projects.modules.driverMongo)
    compileOnly(libs.kotlinx.serialization.json)

    testImplementation(projects.modules.core)
    testImplementation(projects.modules.driverMongo)
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingTest)
    testImplementation(libs.kotlinx.serialization.json)
}
