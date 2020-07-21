package datamaintain.core.step.sort

import datamaintain.core.Context
import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Sorter(private val context: Context) {
    fun sort(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        logger.info { "Sort scripts..." }

        val sortingStrategy = ByCaseInsensitiveSeparatorFreeAlphabeticalSortingStrategy();
        val sortedScripts = sortingStrategy.sort(scripts, Script::identifier)

        logger.info { "Scripts sorted" }
        logger.info { "" }

        return sortedScripts
    }
}