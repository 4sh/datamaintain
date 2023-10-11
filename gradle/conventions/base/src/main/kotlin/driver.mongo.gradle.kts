package datamaintain.conventions

plugins {
    kotlin("jvm") // necessary to declare dependencies
}

dependencies {
    val mongo = project(":modules:driver-mongo")

    compileOnly(mongo)
    testImplementation(mongo)
    testImplementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-test"))
}
