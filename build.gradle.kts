import java.io.ByteArrayOutputStream

object Versions {
    const val junit = "5.5.2"
    const val strikt = "0.21.1"
    const val mockk = "1.9.3"
    const val kmongo = "3.11.1"
    const val mongo = "4.0.13"
}

object Globals {
    const val mongoPort = "27018"
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    application
    maven
    id("com.sourcemuse.mongo") version "1.0.7"
    id("com.palantir.graal") version "0.4.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.litote.kmongo:kmongo:${Versions.kmongo}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.junit}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
}

application {
    mainClassName = "datamaintain.AppKt"
}

mongo {
    setPort(Globals.mongoPort)
    mongoVersion = Versions.mongo
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

tasks.getByPath("test").doFirst {
    with(this as Test) {
        useJUnitPlatform()
    }
}.dependsOn("startManagedMongoDb")
