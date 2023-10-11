plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.palantir.graal) apply false
    alias(libs.plugins.palantir.git) apply false
    alias(libs.plugins.testLogger) apply false
    id("maven-publish")
    signing
}

val modulesToPublish = listOf(
    "core",
    "driver-jdbc",
    "driver-mongo",
    "driver-mongo-mapping-serialization",
    "driver-mongo-mapping-jackson",
    "driver-mongo-mapping-gson",
    "driver-mongo-mapping-test"
)

allprojects {
    apply<com.palantir.gradle.gitversion.GitVersionPlugin>()

    val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
    val lastTag = versionDetails().lastTag

    val computedVersion: String = if (lastTag != "") {
        lastTag
    } else {
        "SNAPSHOT"
    }

    group = "io.github.4sh.datamaintain"
    version = computedVersion

    repositories {
        mavenCentral()
    }

    if (modulesToPublish.contains(this.name)) {
        apply(from = rootProject.file("buildScripts/gradle/publishing.gradle.kts"))
    }
}
