package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Filter(val context: Context) {
    private val filterConfig
        get() = context.config.filter

    fun filter(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        try {
            logger.info { "Filter scripts..." }
            var filteredScripts = scripts

            if (filterConfig.whitelistedTags.isNotEmpty()) {
                logger.trace { "Check whitelisted tags ${filterConfig.whitelistedTags}" }
                filteredScripts = filteredScripts.filter { script ->
                    val kept = filterConfig.whitelistedTags.any { it isIncluded script }

                    if (!kept) {
                        logger.debug { "${script.name} is skipped because not whitelisted" }
                    }

                    kept
                }
            }

            if (filterConfig.blacklistedTags.isNotEmpty()) {
                logger.trace { "Check blacklisted tags ${filterConfig.blacklistedTags}" }
                filteredScripts = filteredScripts.filterNot { script ->
                    val skipped = filterConfig.blacklistedTags.any { it isIncluded script }
                    if (skipped) {
                        logger.debug { "${script.name} is skipped because blacklisted" }
                    }
                    skipped
                }
            }

            filteredScripts = filteredScripts.onEach { context.reportBuilder.addFilteredScript(it) }


            logger.info { "${filteredScripts.size} scripts filtered (${scripts.size - filteredScripts.size} skipped)" }
            logger.trace { filteredScripts.map { it.name } }
            logger.info { "" }
            return filteredScripts
        } catch (datamaintainException: DatamaintainBaseException) {
            throw DatamaintainException(
                datamaintainException.message,
                Step.FILTER,
                context.reportBuilder,
                datamaintainException.resolutionMessage
            )
        }
    }
}
