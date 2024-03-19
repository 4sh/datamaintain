plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
}

dependencies {
    api(projects.modules.domainReport)
//    implementation(projects.modules.monitoring)
}