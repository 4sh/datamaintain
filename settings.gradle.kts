rootProject.name = "datamaintain"

pluginManagement {
    includeBuild("gradle/conventions")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(
        "modules:core",
        "modules:cli",
        "modules:domain-report",
        "modules:monitoring",
        "modules:driver-mongo",
        "modules:driver-mongo-mapping:driver-mongo-mapping-test",
        "modules:driver-mongo-mapping:driver-mongo-mapping-serialization",
        "modules:driver-mongo-mapping:driver-mongo-mapping-jackson",
        "modules:driver-mongo-mapping:driver-mongo-mapping-gson",
        "modules:driver-jdbc",
        "modules:test",
        "samples:java-mongo",
        "samples:java-postgresql",
        "samples:java-spring-boot-postgresql"
    )

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("../datamaintain-monitoring") {
        dependencySubstitution {
                substitute(module("io.github.4sh.datamaintain-monitoring:api-execution-report"))
                        .using(project(":modules:api-execution-report:api"))
        }
}

includeBuild("../datamaintain-monitoring") {
        dependencySubstitution {
                substitute(module("io.github.4sh.datamaintain-monitoring:api-execution-report")).using(project(":modules:api-execution-report:api"))
        }
}