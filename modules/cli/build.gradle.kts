import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.palantir.graal)
    application
    `maven-publish`
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
    api(platform(libs.kotlin.bom))
    implementation(libs.kotlin.jdk8)

    implementation(libs.kotlinLogging)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.strikt)
    testImplementation(libs.mockk)
    testImplementation(libs.testContainers)
    testImplementation(libs.testContainers.jupiter)
    testImplementation(libs.testContainers.mongodb)

    implementation(projects.modules.core)
    implementation(projects.modules.driverMongo)
    implementation(projects.modules.driverMongoMapping.driverMongoMappingSerialization)
    implementation(projects.modules.driverJdbc)

    implementation(libs.clikt)
    implementation(libs.logbackClassic)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(projects.modules.test)
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
