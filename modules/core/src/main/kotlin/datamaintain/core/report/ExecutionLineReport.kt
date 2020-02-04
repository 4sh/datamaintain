package datamaintain.core.report

import datamaintain.core.report.ExecutionLineReport.Companion.correctExecutionMessage
import datamaintain.core.report.ExecutionLineReport.Companion.errorExecutionMessage
import datamaintain.core.report.ExecutionLineReport.Companion.forceMarkMessage
import datamaintain.core.report.ExecutionLineReport.Companion.shouldBeExecutedMessage
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import java.time.Instant

open class ExecutionLineReport(
        override val date: Instant,
        override val message: String,
        override val executionStatus: ExecutionStatus,
        val script: ExecutedScript
) : ScriptLineReport {

    companion object {
        fun correctExecutionMessage(name: String) = "script $name has been executed"
        fun errorExecutionMessage(name: String) = "error during exececution of $name"
        fun forceMarkMessage(name: String) = "script $name has been marked as executed without has been executed"
        fun shouldBeExecutedMessage(name: String) = "script $name should be executed"
    }
}

fun ExecutedScript.toExecutionLineReport() =
        ExecutionLineReport(
                Instant.now(),
                buildMessageReport(),
                executionStatus,
                this
        )

private fun ExecutedScript.buildMessageReport(): String = when (executionStatus) {
    ExecutionStatus.OK -> correctExecutionMessage(name)
    ExecutionStatus.KO -> errorExecutionMessage(name)
    ExecutionStatus.FORCE_MARKED_AS_EXECUTED -> forceMarkMessage(name)
    ExecutionStatus.SHOULD_BE_EXECUTED -> shouldBeExecutedMessage(name)
}


