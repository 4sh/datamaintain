package datamaintain.cli.completion

import java.io.File
import java.io.IOException

data class ProcessResult(val exitValue: Int, val output: String)

fun generateAutoCompletionScripts(outputDir: String) {
    listOf("bash", "zsh").forEach {
        val result = execAppInSubprocess(listOf("generate-completion", it),
                                 outputDir + "/" + it + "-autocomplete.sh")
    }
}

fun main() {
    val outputDir = "../../docs/auto-completion"
    generateAutoCompletionScripts(outputDir)
 }

/**
 * Execute App.kt class as a subprocess. The class is executed with the "java" command.
 * This allows the subprocess to call "exit 1" without crashing the main test process
 *
 * @see <a href="https://lankydan.dev/running-a-kotlin-class-as-a-subprocess">https://lankydan.dev/running-a-kotlin-class-as-a-subprocess</a>
 */
@Throws(IOException::class, InterruptedException::class)
fun execAppInSubprocess(args: List<String> = emptyList(), outputFileName: String): ProcessResult {
    val javaHome = System.getProperty("java.home")
    val javaBin = javaHome + File.separator + "bin" + File.separator + "java"
    val classpath = System.getProperty("java.class.path")
    val className = "datamaintain.cli.app.AppWithCompletionKt"

    val command = ArrayList<String>()
    command.add(javaBin)
    command.add("-cp")
    command.add(classpath)
    command.add(className)
    command.addAll(args)

    // directly write output to the given output file
    val process = ProcessBuilder(command)
            .redirectOutput(File(outputFileName))
            .redirectErrorStream(true)
            .start()

    process.waitFor()
    return ProcessResult(process.exitValue(), "")
}
