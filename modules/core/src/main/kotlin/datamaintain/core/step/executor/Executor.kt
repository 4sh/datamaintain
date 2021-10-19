package datamaintain.core.step.executor

import datamaintain.core.Context
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.exception.DatamaintainScriptExecutionException
import datamaintain.core.report.Report
import datamaintain.core.script.*
import datamaintain.core.step.Step
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Executor(private val context: Context) {

    fun execute(scripts: List<ScriptWithContent>): Report {
        if (!context.config.porcelain) { logger.info { "Executes scripts.." } }
        try {
            for (script in scripts) {
                val executedScript = when (context.config.executionMode) {
                    ExecutionMode.NORMAL -> doAction(script)
                    ExecutionMode.DRY -> simulateAction(script)
                    else -> throw IllegalStateException("Should not be in that case")
                }

                    context.reportBuilder.addReportExecutedScript(
                        ReportExecutedScript.from(
                            executedScript,
                            scripts.first { it.checksum == executedScript.checksum }.porcelainName
                        )
                    )

                if (executedScript.executionStatus == ExecutionStatus.KO) {
                    if (!context.config.porcelain) { logger.info { "" } }
                    // TODO handle interactive shell
                    throw DatamaintainScriptExecutionException(executedScript)
                }
            }

            if (!context.config.porcelain) { logger.info { "" } }
            return context.reportBuilder.toReport()
        } catch (datamaintainException: DatamaintainBaseException) {
            throw DatamaintainException(
                datamaintainException.message,
                Step.EXECUTE,
                context.reportBuilder,
                datamaintainException.resolutionMessage
            )
        }
    }

    private fun doAction(script: ScriptWithContent): ExecutedScript {
        return when (script.action) {
            ScriptAction.RUN -> {
                var execution = Execution(ExecutionStatus.KO)

                val executionDurationInMillis = measureTimeMillis {
                    execution = context.dbDriver.executeScript(script)
                }

                val executedScript = ExecutedScript.build(script, execution, executionDurationInMillis)

                if (executedScript.executionStatus == ExecutionStatus.OK) {
                    markAsExecuted(executedScript)
                    if (!context.config.porcelain) { logger.info { "${executedScript.name} executed" } }
                }

                executedScript
            }
            ScriptAction.MARK_AS_EXECUTED -> {
                val executedScript = ExecutedScript.build(script, Execution(ExecutionStatus.OK))

                markAsExecuted(executedScript)
                if (!context.config.porcelain) { logger.info { "${executedScript.name} only marked as executed (so not executed)" } }

                executedScript
            }
            ScriptAction.OVERRIDE_EXECUTED -> {
                val executedScript = ExecutedScript.build(script, Execution(ExecutionStatus.OK))

                overrideExecuted(executedScript)
                if (!context.config.porcelain) { logger.info { "${executedScript.name} only marked as executed (so not executed)" } }

                executedScript
            }
        }
    }

    private fun simulateAction(script: ScriptWithContent): ExecutedScript {
        when (script.action) {
            ScriptAction.RUN ->
                if (!context.config.porcelain) { logger.info { "${script.name} would have been executed" } }
            ScriptAction.MARK_AS_EXECUTED ->
                if (!context.config.porcelain) { logger.info { "${script.name} would have been only marked as executed (so not executed)" } }
        }

        return ExecutedScript.simulateExecuted(script, ExecutionStatus.OK)
    }

    private fun markAsExecuted(it: ExecutedScript) {
        try {
            context.dbDriver.markAsExecuted(it)
        } catch (e: Exception) {
            if (!context.config.porcelain) { logger.error { "error during mark execution of ${it.name} " } }
            throw e
            // TODO handle interactive shell
        }
    }

    private fun overrideExecuted(it: ExecutedScript) {
        try {
            context.dbDriver.overrideScript(it)
        } catch (e: Exception) {
            if (!context.config.porcelain) { logger.error { "error during override of ${it.fullName()} " } }
            throw e
        }
    }
}
