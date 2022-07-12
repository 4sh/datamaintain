package datamaintain.core.report

import datamaintain.core.step.Step
import datamaintain.domain.report.Report
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Report.print(verbose: Boolean, porcelain: Boolean = false) {
    val stepWithMaxExecutionOrder: Step = Step.values().asSequence().maxByOrNull { step -> step.executionOrder }!!
    print(verbose, porcelain, stepWithMaxExecutionOrder)
}

fun Report.print(verbose: Boolean, porcelain: Boolean, maxStepToShow: Step) {
    if (!porcelain) {
        logger.info { "Summary => " }

        // Scanner
        logger.info { "- ${scannedScripts.size} files scanned" }
        if (verbose) {
            scannedScripts.forEach { logger.info { " -> ${it.name}" } }
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
    }

    if (Step.EXECUTE.isSameStepOrExecutedBefore(maxStepToShow)) {
        if (!porcelain) {
            logger.info { "- ${executedScripts.size} files executed" }
        }
        executedScripts.forEach {
            logger.info {
                if (!porcelain) {
                    " -> ${it.name}"
                } else {
                    "${it.porcelainName}"
                }
            }
        }
    }
}