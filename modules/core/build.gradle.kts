plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}
