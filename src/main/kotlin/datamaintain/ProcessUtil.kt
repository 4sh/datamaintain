package datamaintain

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun List<String>.runProcess(
        workingDir: File = File("."),
        timeoutValue: Long = 60,
        timeoutUnit: TimeUnit = TimeUnit.MINUTES
): ProcessResult {
    try {
        val proc = ProcessBuilder(*this.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        proc.waitFor(timeoutValue, timeoutUnit)
        return ProcessResult(proc.inputStream.bufferedReader().readText(),
                proc.exitValue())
    } catch (e: IOException) {
        throw IllegalStateException(e.message, e)
    }
}

data class ProcessResult(val output: String, val exitCode: Int)