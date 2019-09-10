import java.io.*

object Versions {
    val junit = "5.5.2"
    val strikt = "0.21.1"
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    application
    maven
    id("com.palantir.graal") version "0.4.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.junit}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
}

application {
    mainClassName = "datamaintain.AppKt"
}

graal {
    mainClass("datamaintain.AppKt")
    outputName("datamaintain")
}

tasks.register<Exec>("checkNative") {
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