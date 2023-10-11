plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")

    alias(libs.plugins.testLogger)
}

dependencies {
    compileOnly(projects.modules.core)
    compileOnly(projects.modules.driverMongo)
    compileOnly(libs.junit.jupiter.api)

    implementation(libs.jsonPath)
}
