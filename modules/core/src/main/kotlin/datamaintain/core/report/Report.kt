package datamaintain.core.report

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.Step
import datamaintain.core.step.check.rules.CheckRule
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Report @JvmOverloads constructor(
        val scannedScripts: List<ScriptWithContent> = listOf(),
        val filteredScripts: List<ScriptWithContent> = listOf(),
        val prunedScripts: List<ScriptWithContent> = listOf(),
        val executedScripts: List<ExecutedScript> = listOf(),
        val validatedCheckRules: List<CheckRule> = listOf()
) {
    fun print(verbose: Boolean) {
        val stepWithMaxExecutionOrder: Step = Step.values().asSequence().maxBy { step -> step.executionOrder }!!
        print(verbose, stepWithMaxExecutionOrder)
    }

    fun print(verbose: Boolean, maxStepToShow: Step) {
        logger.info { "Summary => " }

        // Scanner
        logger.info { "- ${scannedScripts.size} files scanned" }
        if (verbose) {
            scannedScripts.forEach {logger.info { " -> ${it.name}" }}
        }

        if (Step.FILTER.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.info { "- ${filteredScripts.size} files filtered" }
            if (verbose) {
                filteredScripts.forEach { logger.info { " -> ${it.name}" } }
            }
        }

        if (Step.PRUNE.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.info { "- ${prunedScripts.size} files pruned" }
            if (verbose) {
                prunedScripts.forEach { logger.info { " -> ${it.name}" } }
            }
        }

        if (Step.CHECK.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.info { "- ${validatedCheckRules.size} check rules validated" }
            if (verbose) {
                validatedCheckRules.forEach { logger.info { " -> ${it.getName()}" } }
            }
        }

        if (Step.EXECUTE.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.info { "- ${executedScripts.size} files executed" }
            executedScripts.forEach { logger.info { " -> ${it.name}" } }
        }
    }
}


class ReportBuilder @JvmOverloads constructor(
        private val scannedScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val filteredScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val prunedScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val executedScripts: MutableList<ExecutedScript> = mutableListOf(),
        private val validatedCheckRules: MutableList<CheckRule> = mutableListOf()
) {

    fun addScannedScript(script: ScriptWithContent) {
        scannedScripts.add(script)
    }

    fun addFilteredScript(script: ScriptWithContent) {
        filteredScripts.add(script)
    }

    fun addPrunedScript(script: ScriptWithContent) {
        prunedScripts.add(script)
    }

    fun addExecutedScript(script: ExecutedScript) {
        executedScripts.add(script)
    }

    fun addValidatedCheckRules(checkRule: CheckRule) {
        validatedCheckRules.add(checkRule)
    }

    fun toReport() = Report(
            scannedScripts,
            filteredScripts,
            prunedScripts,
            executedScripts,
            validatedCheckRules
    )
}
