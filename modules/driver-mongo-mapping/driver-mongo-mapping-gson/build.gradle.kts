plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
    id("datamaintain.conventions.driver")
    id("datamaintain.conventions.driver.mongo")

    alias(libs.plugins.testLogger)
}

dependencies {
    compileOnly(libs.gson)
    testImplementation(libs.gson)
}
