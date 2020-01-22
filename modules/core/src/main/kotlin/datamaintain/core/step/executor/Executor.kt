package datamaintain.core.step.executor

import datamaintain.core.Context
import datamaintain.core.report.ExecutionLineReport
import datamaintain.core.report.ExecutionReport
import datamaintain.core.report.toExecutionLineReport
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging
import java.time.Instant

private val logger = KotlinLogging.logger {}

class Executor(private val context: Context) {

    fun execute(scripts: List<ScriptWithContent>): ExecutionReport {
        val reportLines = scripts
                .map {
                    when (context.config.executionMode) {
                        ExecutionMode.NORMAL -> context.dbDriver.executeScript(it)
                        ExecutionMode.FORCE_MARK_AS_EXECUTED -> ExecutedScript.forceMarkAsExecuted(it)
                        ExecutionMode.DRY -> ExecutedScript.shouldBeExecuted(it)
                    }
                }
                .map {
                    if (it.executionStatus == ExecutionStatus.OK) {
                        markAsExecuted(it)
                    } else {
                        it.toExecutionLineReport()
                    }
                }
        return ExecutionReport(reportLines, Instant.now())
    }

    private fun markAsExecuted(it: ExecutedScript): ExecutionLineReport {
        return try {
            context.dbDriver.markAsExecuted(it).toExecutionLineReport()
        } catch (e: Exception) {
            logger.error { "error during execution of ${it.name} " }
            throw e
            // TODO handle interactive shell
        }
    }
}
