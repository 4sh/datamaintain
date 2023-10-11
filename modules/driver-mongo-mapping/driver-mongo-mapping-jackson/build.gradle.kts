plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")

    alias(libs.plugins.testLogger)
}

dependencies {
    compileOnly(projects.modules.core)
    compileOnly(projects.modules.driverMongo)
    compileOnly(libs.jackson.databind)

    testImplementation(projects.modules.core)
    testImplementation(projects.modules.driverMongo)
    testImplementation(projects.modules.driverMongoMapping.driverMongoMappingTest)
    testImplementation(libs.jackson.databind)
}
