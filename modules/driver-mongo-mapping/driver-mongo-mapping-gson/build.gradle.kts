plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}

dependencies {
    compileOnly(projects.modules.core)
    compileOnly(projects.modules.driverMongo)
    compileOnly(libs.gson)

    testImplementation(projects.modules.core)
    testImplementation(projects.modules.driverMongo)
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingTest)
    testImplementation(libs.gson)
}
