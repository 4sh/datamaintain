package datamaintain.core.step.executor

import datamaintain.core.Context
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.exception.DatamaintainScriptExecutionException
import datamaintain.core.step.Step
import datamaintain.domain.report.Report
import datamaintain.domain.script.*
import datamaintain.core.util.exception.DatamaintainQueryException
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
                logger.info {"start execution of script ${script.name}"}
                var execution = Execution(ExecutionStatus.KO)

                val executionDurationInMillis = measureTimeMillis {
                    execution = context.dbDriver.executeScript(script)
                }

                val executedScript = buildExecutedScript(script, execution, executionDurationInMillis, context.config.flags)

                if (executedScript.executionStatus == ExecutionStatus.OK) {
                    markAsExecuted(executedScript)
                    if (!context.config.porcelain) { logger.info { "${executedScript.name} executed" } }
                }

                executedScript
            }
            ScriptAction.MARK_AS_EXECUTED -> {
                logger.info {"start marking script ${script.name} as executed"}
                val executedScript = buildExecutedScript(script, Execution(ExecutionStatus.OK), context.config.flags)

                try {
                    markAsExecuted(executedScript)
                    if (!context.config.porcelain) { logger.info { "${executedScript.name} only marked as executed (so not executed)" } }
                } catch (e: DatamaintainQueryException) {
                    logger.warn { "Failed to mark script ${executedScript.name} as executed. Use the following command to force mark the script as executed : " +
                            "./datamaintain-cli --db-type ${context.config.driverConfig.dbType} --db-uri ${context.config.driverConfig.uri} update-db --path ${context.config.path} --action MARK_AS_EXECUTED" }
                }

                executedScript
            }
            ScriptAction.OVERRIDE_EXECUTED -> {
                logger.info {"start overriding script ${script.name} execution"}
                val executedScript = buildExecutedScript(script, Execution(ExecutionStatus.OK), context.config.flags)

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

        return buildSimulatedExecutedScript(script, ExecutionStatus.OK, context.config.flags)
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
            if (!context.config.porcelain) {
                logger.error { "error during override of ${it.fullName()} " }
            }
            throw e
        }
    }
}

fun buildSimulatedExecutedScript(script: ScriptWithContent, executionStatus: ExecutionStatus, flags: List<String>) =
    ExecutedScript(
        script.name,
        script.checksum,
        script.identifier,
        executionStatus,
        script.action,
        flags = flags
    )

fun buildExecutedScript(script: ScriptWithContent, execution: Execution, flags: List<String>) =
    buildSimulatedExecutedScript(script, execution.executionStatus, flags)

fun buildExecutedScript(script: ScriptWithContent, execution: Execution, executionDurationInMillis: Long, flags: List<String>) =
    ExecutedScript(
        script.name,
        script.checksum,
        script.identifier,
        execution.executionStatus,
        script.action,
        executionDurationInMillis,
        execution.executionOutput,
        flags = flags
    )
