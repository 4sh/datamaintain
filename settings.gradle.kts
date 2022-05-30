rootProject.name = "datamaintain"
include(
        "modules:core",
        "modules:cli",
        "modules:driver-mongo",
        "modules:driver-mongo-mapping:driver-mongo-mapping-serialization",
        "modules:driver-jdbc",
        "modules:test",
        "samples:java-mongo",
        "samples:java-postgresql"
)
