package datamaintain.core.util

import datamaintain.core.exception.DatamaintainProcessException
import mu.KotlinLogging
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

fun List<String>.runProcess(
        workingDir: File = File("."),
        timeoutValue: Long = 60,
        timeoutUnit: TimeUnit = TimeUnit.MINUTES,
        outputReadFunction: ((Sequence<String>) -> Unit)? = null
): Int {
    try {
        logger.trace { "executing shell command: '${this.joinToString(" ")}'" }
        val proc = ProcessBuilder(*this.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        // capture and print output if trace level is activated
        val output = if (logger.isTraceEnabled)
            proc.inputStream.bufferedReader().lineSequence().onEach { logger.trace { it } }
        else
            proc.inputStream.bufferedReader().lineSequence()

        if (outputReadFunction != null) {
            outputReadFunction(output)
        } else {
            output.forEach { }
        }

        proc.waitFor(timeoutValue, timeoutUnit)
        val exitValue = proc.exitValue()
        logger.trace { "shell command done with exit code: $exitValue" }
        return exitValue
    } catch (e: IOException) {
        logger.trace { "shell command error: $e" }
        throw DatamaintainProcessException(this, e.message ?: "")
    }
}
