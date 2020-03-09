package datamaintain.core.util

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

fun List<String>.runProcess(
        workingDir: File = File("."),
        timeoutValue: Long = 60,
        timeoutUnit: TimeUnit = TimeUnit.MINUTES,
        outputReadFunction: ((InputStream) -> Unit)? = null
): Int {
    try {
        val proc = ProcessBuilder(*this.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
        if (outputReadFunction != null) {
            outputReadFunction(proc.inputStream)
        }
        proc.waitFor(timeoutValue, timeoutUnit)
        return  proc.exitValue()
    } catch (e: IOException) {
        throw IllegalStateException(e.message, e)
    }
}
