package datamaintain.core.step.sort

import datamaintain.core.Context
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.Step
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Sorter(private val context: Context) {
    fun sort(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        try {
            if (!context.config.porcelain) { logger.info { "Sort scripts..." } }

            val sortingStrategy = ByCaseInsensitiveSeparatorFreeAlphabeticalSortingStrategy()
            val sortedScripts = sortingStrategy.sort(scripts, Script::identifier)

            if (!context.config.porcelain) {
                logger.info { "Scripts sorted" }
                logger.info { "" }
            }

            return sortedScripts
        } catch (datamaintainException: DatamaintainBaseException) {
            throw DatamaintainException(
                datamaintainException.message,
                Step.SORT,
                context.reportBuilder,
                datamaintainException.resolutionMessage
            )
        }
    }
}
