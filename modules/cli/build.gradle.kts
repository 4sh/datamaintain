import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.*

plugins {
    id("org.jetbrains.kotlin.jvm")
    application
    `maven-publish`
    id("com.palantir.graal")
}

dependencies {
    "api"((this.platform("org.jetbrains.kotlin:kotlin-bom:${Versions.kotlin}")))
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

dependencies {
    "implementation"("io.github.microutils:kotlin-logging:${Versions.kotlinLogging}")

    "testImplementation"("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    "testImplementation"("org.junit.jupiter:junit-jupiter-params:${Versions.junit}")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
    "testImplementation"("io.strikt:strikt-core:${Versions.strikt}")
    "testImplementation"("io.mockk:mockk:${Versions.mockk}")
    "testImplementation"("org.testcontainers:testcontainers:${Versions.testcontainers}")
    "testImplementation"("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    "testImplementation"("org.testcontainers:mongodb:${Versions.testcontainers}")
}

tasks.getByPath("test").doFirst({
    with<org.gradle.api.tasks.testing.Test, kotlin.Unit>(this as Test) {
        this.useJUnitPlatform()
    }
})

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation(project(":modules:driver-mongo-mapping:driver-mongo-mapping-serialization"))
    implementation(project(":modules:driver-jdbc"))

    implementation("com.github.ajalt:clikt:${Versions.clikt}")
    implementation("ch.qos.logback:logback-classic:${Versions.logbackClassic}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")

    testImplementation(project(":modules:test"))
}

application {
    mainClass.set("datamaintain.cli.app.AppKt")
}

graal {
    mainClass("datamaintain.cli.app.AppKt")
    outputName("datamaintain")
}

tasks.startScripts {
    doLast {
        val scriptFile = File("${outputDir}/${applicationName}")
        scriptFile.writeText(scriptFile.readText().replace("CLASSPATH=", "CLASSPATH=:\$APP_HOME/lib/drivers/*:"))
    }
}

tasks.installDist {
    dependsOn("generateVersionProperties")
    doLast {
        mkdir("$destinationDir/lib/drivers")
    }
}

tasks.register<Exec>("graalCheckNative") {
    dependsOn("nativeImage")

    val expected = "Usage: app [OPTIONS]"

    commandLine = listOf("./build/graal/datamaintain", "--help")
    standardOutput = ByteArrayOutputStream()

    doLast {
        val out = (standardOutput as ByteArrayOutputStream).toString("UTF-8")
        if (out.startsWith(expected)) {
            println("Native image check: OK")
        } else {
            println("Native image check: NOK")
            println("output:\n`$out`")
            throw RuntimeException("Execution of native image failed. Output was:\n$out")
        }
    }
}

task("rebuildCliDocumentation", JavaExec::class) {
    mainClass.set("datamaintain.cli.documentation.RebuildDocumentationKt")
    classpath = sourceSets["main"].runtimeClasspath
}

val generatedVersionDir = "${buildDir}/generated-version"

sourceSets {
    main {
        kotlin {
            output.dir(generatedVersionDir)
        }
    }
}

task("generateVersionProperties") {
    val env = System.getProperty("env")
    if (env == "prod") {
        delete("$generatedVersionDir/version.properties")
        doLast {
            val propertiesFile = file("$generatedVersionDir/version.properties")
            propertiesFile.parentFile.mkdirs()
            val properties = Properties()
            properties.setProperty("version", rootProject.version.toString())
            val out = FileOutputStream(propertiesFile)
            properties.store(out, null)
        }
    } else {
        delete("$generatedVersionDir/version.properties")
    }
}
