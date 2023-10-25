plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.testLogger)
}

dependencies {
    implementation(projects.modules.cli)
    implementation(projects.modules.core)
    implementation(projects.modules.driverMongo)
    implementation(libs.mongoDriver.sync)
    testImplementation(libs.testContainers)
    testImplementation(libs.testContainers.jupiter)
    testImplementation(libs.testContainers.mongodb)
}
