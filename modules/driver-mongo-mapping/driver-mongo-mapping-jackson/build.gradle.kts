plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
    id("datamaintain.conventions.driver")
    id("datamaintain.conventions.driver.mongo")
}

dependencies {
    compileOnly(libs.jackson.databind)
    testImplementation(libs.jackson.databind)
}
