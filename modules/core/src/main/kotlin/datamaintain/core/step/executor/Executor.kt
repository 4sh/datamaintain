package datamaintain.core.step.executor

import datamaintain.core.Context
import datamaintain.core.report.Report
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ScriptAction
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Executor(private val context: Context) {

    fun execute(scripts: List<ScriptWithContent>): Report {
        logger.info { "Executes scripts.." }

        for (script in scripts) {
            val executedScript = when (context.config.executionMode) {
                ExecutionMode.NORMAL -> doAction(script)
                else -> simulateAction(script)
            }

            context.reportBuilder.addExecutedScript(executedScript)

            when (executedScript.executionStatus) {
                ExecutionStatus.KO -> {
                    context.reportBuilder.inError(executedScript)
                    logger.info { "${executedScript.name} has not been correctly executed" }
                    // TODO handle interactive shell
                    return context.reportBuilder.toReport()
                }
                else -> {}
            }
        }

        logger.info { "" }
        return context.reportBuilder.toReport()
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
                    logger.info { "${executedScript.name} executed" }
                }

                executedScript
            }
            ScriptAction.MARK_AS_EXECUTED -> {
                val executedScript = ExecutedScript.build(script, Execution(ExecutionStatus.OK))

                markAsExecuted(executedScript)
                logger.info { "${executedScript.name} only marked as executed (so not executed)" }

                executedScript
            }
            ScriptAction.OVERRIDE_EXECUTED -> {
                val executedScript = ExecutedScript.build(script, Execution(ExecutionStatus.OK))

                overrideExecuted(executedScript)
                logger.info { "${executedScript.name} only marked as executed (so not executed)" }

                executedScript
            }
        }
    }

    private fun simulateAction(script: ScriptWithContent): ExecutedScript {
        when (script.action) {
            ScriptAction.RUN ->
                logger.info { "${script.name} would have been executed" }
            ScriptAction.MARK_AS_EXECUTED ->
                logger.info { "${script.name} would have been only marked as executed (so not executed)" }
        }

        return ExecutedScript.simulateExecuted(script, ExecutionStatus.OK)
    }

    private fun markAsExecuted(it: ExecutedScript) {
        try {
            context.dbDriver.markAsExecuted(it)
        } catch (e: Exception) {
            logger.error { "error during mark execution of ${it.name} " }
            throw e
            // TODO handle interactive shell
        }
    }

    private fun overrideExecuted(it: ExecutedScript) {
        try {
            context.dbDriver.overrideScript(it)
        } catch (e: Exception) {
            logger.error { "error during override of ${it.fullName()} " }
            throw e
        }
    }
}
