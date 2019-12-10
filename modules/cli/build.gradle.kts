import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.kotlin.jvm")
    application
    maven
    id("com.sourcemuse.mongo")
    id("com.palantir.graal")
}

baseProject()

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation("com.github.ajalt:clikt:${Versions.clikt}")

}

application {
    mainClassName = "datamaintain.AppKt"
}

graal {
    mainClass("datamaintain.AppKt")
    outputName("datamaintain")
}

tasks.register<Exec>("graalCheckNative") {
    dependsOn("nativeImage")

    val expected = "Hello world.\n"

    commandLine = listOf("./build/graal/datamaintain")
    standardOutput = ByteArrayOutputStream()

    doLast {
        val out = (standardOutput as ByteArrayOutputStream).toString("UTF-8")
        if (out != expected) {
            println("Native image check: NOK")
            println("output:\n`$out`")
            throw RuntimeException("Execution of native image failed. Output was:\n$out")
        } else {
            println("Native image check: OK")
        }
    }
}

