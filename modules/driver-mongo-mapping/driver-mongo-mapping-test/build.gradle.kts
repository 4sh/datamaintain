plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
    id("datamaintain.conventions.driver")
}

dependencies {
    compileOnly(projects.modules.driverMongo)
    compileOnly(libs.junit.jupiter.api)

    implementation(libs.jsonPath)
}
