plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")

    alias(libs.plugins.testLogger)
}

dependencies {
    compileOnly(projects.modules.core)
    testImplementation(projects.modules.core)

    testImplementation(libs.mongoDriver.sync)

    // Use kotlinx-serialization implementation
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingSerialization)
    testImplementation(libs.kotlinx.serialization.json)
}
