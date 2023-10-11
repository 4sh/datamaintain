import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.*

plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.palantir.graal)

    application
    `maven-publish`
}

dependencies {
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

tasks.register("rebuildCliDocumentation", JavaExec::class) {
    mainClass.set("datamaintain.cli.documentation.RebuildDocumentationKt")
    classpath = sourceSets["main"].runtimeClasspath
}

val generatedVersionDir = project.layout.buildDirectory.dir("generated-version")

sourceSets {
    main {
        kotlin {
            output.dir(generatedVersionDir)
        }
    }
}

tasks.register("generateVersionProperties") {
    doLast {
        val env = System.getProperty("env")
        if (env == "prod") {
            delete("$generatedVersionDir/version.properties")
            val propertiesFile = file("$generatedVersionDir/version.properties")
            propertiesFile.parentFile.mkdirs()
            val properties = Properties()
            properties.setProperty("version", rootProject.version.toString())
            val out = FileOutputStream(propertiesFile)
            properties.store(out, null)
        } else {
            delete("$generatedVersionDir/version.properties")
        }
    }
}
