rootProject.name = "datamaintain"
include(
        "modules:core",
        "modules:cli",
        "modules:domain-report",
        "modules:driver-mongo",
        "modules:driver-jdbc",
        "modules:test",
        "samples:java-mongo",
        "samples:java-postgresql"
)