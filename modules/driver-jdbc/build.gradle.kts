plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}

dependencies {
    compileOnly(projects.modules.core)
    testImplementation(projects.modules.core)
    testImplementation(libs.h2database)
}
