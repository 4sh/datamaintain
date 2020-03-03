package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Filter(private val context: Context) {
    fun filter(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        logger.info { "Filter scripts..." }
        val filteredScripts = scripts
                .filterNot { script ->
                    val skipped = context.config.blacklistedTags.any { it isIncluded script }
                    if (context.config.verbose && skipped) {
                        logger.info { "${script.name} is skipped" }
                    }
                    skipped
                }
                .onEach { context.reportBuilder.addFilteredScript(it) }
        logger.info { "${filteredScripts.size} scripts filtered (${scripts.size - filteredScripts.size} skipped)" }
        logger.info { "" }
        return filteredScripts
    }
}
