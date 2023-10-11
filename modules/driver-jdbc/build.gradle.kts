plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
    id("datamaintain.conventions.driver")

    alias(libs.plugins.testLogger)
}

dependencies {
    testImplementation(libs.h2database)
}
