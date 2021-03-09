package datamaintain.test

import java.io.File
import java.io.IOException
import java.lang.StringBuilder


data class ProcessResult(val exitValue: Int, val output: String)

/**
 * Execute App.kt class as a subprocess. The class is execute with java command.
 * This allow the subprocess to call "exit 1" without crashing the main test process
 *
 * @see <a href="https://lankydan.dev/running-a-kotlin-class-as-a-subprocess">https://lankydan.dev/running-a-kotlin-class-as-a-subprocess</a>
 */
@Throws(IOException::class, InterruptedException::class)
fun execAppInSubprocess(args: List<String> = emptyList(), jvmArgs: List<String> = emptyList()): ProcessResult {
    val javaHome = System.getProperty("java.home")
    val javaBin = javaHome + File.separator + "bin" + File.separator + "java"
    val classpath = System.getProperty("java.class.path")
    val className = "datamaintain.cli.AppCli"

    val command = ArrayList<String>()
    command.add(javaBin)
    command.addAll(jvmArgs)
    command.add("-cp")
    command.add(classpath)
    command.add(className)
    command.addAll(args)

    val process = ProcessBuilder(command)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .redirectErrorStream(true)
        .start()

    // Read output, print it and add it to processOutput
    val processOutput = StringBuilder()
    process.inputStream.bufferedReader().lines()
        .peek { processOutput.append(it).append(System.lineSeparator()) }
        .forEach { println(it) }

    process.waitFor()
    return ProcessResult(process.exitValue(), processOutput.toString())
}
