package datamaintain.cli.completion

import java.io.File
import java.io.IOException

fun generateAutoCompletionScripts(outputDir: String) {
    listOf("bash", "zsh").forEach {
        generateAutoCompletionForShell(it, outputDir + "/" + it + "-autocomplete.sh")
    }
}

fun main() {
    val outputDir = "../../docs/auto-completion"
    generateAutoCompletionScripts(outputDir)
 }

/**
 * Execute DatamaintainCLI.kt class as a subprocess. The class is executed with the "java" command.
 * This allows the subprocess to call "exit 1" without crashing the main test process
 *
 * @see <a href="https://ajalt.github.io/clikt/autocomplete/#with-an-environment-variable">Clikt Documention</a>
 * @see <a href="https://lankydan.dev/running-a-kotlin-class-as-a-subprocess">https://lankydan.dev/running-a-kotlin-class-as-a-subprocess</a>
 */
@Throws(IOException::class, InterruptedException::class)
fun generateAutoCompletionForShell(shell: String, outputFileName: String) {
    val outfile = File(outputFileName)

    println("Generate $shell autocomplete file to ${outfile.toPath().toAbsolutePath().normalize()}")
    val javaHome = System.getProperty("java.home")
    val javaBin = javaHome + File.separator + "bin" + File.separator + "java"
    val classpath = System.getProperty("java.class.path")
    val className = "datamaintain.cli.app.DatamaintainCLIKt"

    val command = ArrayList<String>()
    command.add("/usr/bin/env")
    command.add("sh")
    command.add("-c")
    command.add("_DATAMAINTAIN_COMPLETE=$shell $javaBin -cp $classpath $className")

    // directly write output to the given output file
    val process = ProcessBuilder(command)
            .redirectOutput(outfile)
            .redirectErrorStream(true)
            .start()

    process.waitFor()
}
