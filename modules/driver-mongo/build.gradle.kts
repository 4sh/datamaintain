plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
    id("datamaintain.conventions.driver")
}

dependencies {
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingSerialization)

    testImplementation(libs.mongoDriver.sync)

    // Use kotlinx-serialization implementation
    testImplementation(libs.kotlinx.serialization.json)

    testImplementation(libs.testContainers)
    testImplementation(libs.testContainers.jupiter)
    testImplementation(libs.testContainers.mongodb)
}
