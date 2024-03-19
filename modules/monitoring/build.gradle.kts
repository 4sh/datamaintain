plugins {
    id("datamaintain.conventions.kotlin")
    id("datamaintain.conventions.publishing")
}

dependencies {
    implementation(projects.modules.domain)
    implementation(libs.http4kCore)
    implementation(libs.http4kJackson)
    implementation(libs.jackson.datatype.jsr310)
    implementation("io.github.4sh.datamaintain-monitoring:api:unspecified")
}

