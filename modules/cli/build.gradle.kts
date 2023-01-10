import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.*

plugins {
    id("org.jetbrains.kotlin.jvm")
    application
    `maven-publish`
    id("com.palantir.graal")
}

baseProject()

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-mongo"))
    implementation(project(":modules:driver-jdbc"))
    testImplementation(project(":modules:test"))
    implementation("com.github.ajalt.clikt:clikt:${Versions.clikt}")
    implementation("ch.qos.logback:logback-classic:${Versions.logbackClassic}")
}

application {
    mainClass.set("datamaintain.cli.app.DatamaintainCLIKt")
}

graal {
    mainClass("datamaintain.cli.app.DatamaintainCLIKt")
    outputName("datamaintain")
}

tasks.startScripts {
    doLast {
        val scriptFile = File("${outputDir}/${applicationName}")
        scriptFile.writeText(scriptFile.readText().replace("CLASSPATH=", "CLASSPATH=:\$APP_HOME/lib/drivers/*:"))
    }
}

tasks.jar {
    dependsOn("rebuildAutocompletion")
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
    main = "datamaintain.cli.documentation.RebuildDocumentationKt"
    classpath = sourceSets["main"].runtimeClasspath
}

task("rebuildAutocompletion", JavaExec::class) {
    main = "datamaintain.cli.completion.RebuildAutoCompletionScriptsKt"
    classpath = sourceSets["main"].runtimeClasspath
    outputs.upToDateWhen { false }
}

val generatedVersionDir = "${buildDir}/generated-version"

sourceSets {
    main {
        kotlin {
            output.dir(generatedVersionDir)
        }
    }
}
distributions {
    main {
        contents {
            from("../../docs/auto-completion/bash-autocomplete.sh")
            from("../../docs/auto-completion/zsh-autocomplete.sh")
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

