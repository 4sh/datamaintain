plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.palantir.graal) apply false
    alias(libs.plugins.palantir.git)
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
    if (modulesToPublish.contains(this.name)) {
        apply(from = rootProject.file("buildScripts/gradle/publishing.gradle.kts"))
    }
}
