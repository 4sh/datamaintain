package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Filter(private val context: Context) {
    fun filter(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        logger.info { "Filter scripts..." }
        val filteredScripts = scripts
                .filterNot { script -> context.config.blacklistedTags.any { it matchedBy script } }
                .onEach { context.reportBuilder.addFilteredScript(it) }
        logger.info { "${filteredScripts.size} scripts filtered (${scripts.size - filteredScripts.size} skipped)" }
        logger.info { "" }
        return filteredScripts
    }
}
