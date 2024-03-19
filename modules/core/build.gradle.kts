plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
}

dependencies {
    api(projects.modules.domain)
    implementation(projects.modules.monitoring)
}