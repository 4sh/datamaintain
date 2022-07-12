package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.domain.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Filter(private val context: Context) {
    private val filterConfig
        get() = context.config.filter

    fun filter(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        try {
            if (!context.config.logs.porcelain) {
                logger.info { "Filter scripts..." }
            }
            var filteredScripts = scripts

            if (filterConfig.whitelistedTags.isNotEmpty()) {
                filteredScripts = filteredScripts.filter { script ->
                    val kept = filterConfig.whitelistedTags.any { it isIncluded script }

                    if (context.config.logs.verbose && !kept && !context.config.logs.porcelain) {
                        logger.info { "${script.name} is skipped because not whitelisted" }
                    }

                    kept
                }
            }

            if (filterConfig.blacklistedTags.isNotEmpty()) {
                filteredScripts = filteredScripts.filterNot { script ->
                    val skipped = filterConfig.blacklistedTags.any { it isIncluded script }
                    if (context.config.logs.verbose && skipped && !context.config.logs.porcelain) {
                        logger.info { "${script.name} is skipped because blacklisted" }
                    }
                    skipped
                }
            }

            filteredScripts = filteredScripts.onEach { context.reportBuilder.addFilteredScript(it) }

            if (!context.config.logs.porcelain) {
                logger.info { "${filteredScripts.size} scripts filtered (${scripts.size - filteredScripts.size} skipped)" }
                logger.info { "" }
            }
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
