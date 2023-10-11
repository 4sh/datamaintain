rootProject.name = "datamaintain"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

include(
        "modules:core",
        "modules:cli",
        "modules:driver-mongo",
        "modules:driver-mongo-mapping:driver-mongo-mapping-test",
        "modules:driver-mongo-mapping:driver-mongo-mapping-serialization",
        "modules:driver-mongo-mapping:driver-mongo-mapping-jackson",
        "modules:driver-mongo-mapping:driver-mongo-mapping-gson",
        "modules:driver-jdbc",
        "modules:test",
        "samples:java-mongo",
        "samples:java-postgresql"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
