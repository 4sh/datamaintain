plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
    id("datamaintain.conventions.driver")
}

dependencies {
    testImplementation(libs.h2database)
}
