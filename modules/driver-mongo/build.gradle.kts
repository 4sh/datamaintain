plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
    id("datamaintain.conventions.driver")
}

dependencies {
    testImplementation(libs.mongoDriver.sync)

    // Use kotlinx-serialization implementation
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingSerialization)
    testImplementation(libs.kotlinx.serialization.json)
}
