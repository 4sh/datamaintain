package datamaintain.core.report

import datamaintain.core.script.ReportExecutedScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.Step
import datamaintain.core.step.check.rules.CheckRule
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Report @JvmOverloads constructor(
        val scannedScripts: List<ScriptWithContent> = listOf(),
        val filteredScripts: List<ScriptWithContent> = listOf(),
        val prunedScripts: List<ScriptWithContent> = listOf(),
        val executedScripts: List<ReportExecutedScript> = listOf(),
        val validatedCheckRules: List<CheckRule> = listOf()
) {
    fun print() {
        val stepWithMaxExecutionOrder: Step = Step.values().asSequence().maxByOrNull { step -> step.executionOrder }!!
        print(stepWithMaxExecutionOrder)
    }

    fun print(maxStepToShow: Step) {
        logger.info { "Summary => " }

        // Scanner
        logger.debug { "- ${scannedScripts.size} files scanned" }
        scannedScripts.forEach { logger.trace { " -> ${it.name}" } }

        if (Step.FILTER.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.debug { "- ${filteredScripts.size} files filtered" }
            filteredScripts.forEach { logger.trace { " -> ${it.name}" } }
        }

        if (Step.PRUNE.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.debug { "- ${prunedScripts.size} files pruned" }
            prunedScripts.forEach { logger.trace { " -> ${it.name}" } }
        }

        if (Step.CHECK.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.debug { "- ${validatedCheckRules.size} check rules validated" }
            validatedCheckRules.forEach { logger.trace { " -> ${it.getName()}" } }
        }

        if (Step.EXECUTE.isSameStepOrExecutedBefore(maxStepToShow)) {
            logger.info { "- ${executedScripts.size} files executed" }
            executedScripts.forEach {
                logger.info { " -> ${it.name}" }
            }
        }
    }
}


class ReportBuilder @JvmOverloads constructor(
        private val scannedScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val filteredScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val prunedScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val executedScripts: MutableList<ReportExecutedScript> = mutableListOf(),
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

    fun addReportExecutedScript(script: ReportExecutedScript) {
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
