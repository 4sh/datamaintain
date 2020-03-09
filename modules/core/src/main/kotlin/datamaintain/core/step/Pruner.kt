package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Pruner(private val context: Context) {
    fun prune(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        logger.info { "Prune scripts..." }
        val executedChecksums = context.dbDriver.listExecutedScripts()
                .map { executedScript -> executedScript.checksum }
                .toList()
        val prunedScripts = scripts
                .filterNot { script ->
                    val skipped = executedChecksums.contains(script.checksum)
                    if (context.config.verbose && skipped) {
                        logger.info { "${script.name} is skipped" }
                    }
                    skipped
                }
                .onEach { context.reportBuilder.addPrunedScript(it) }

        logger.info { "${prunedScripts.size} scripts pruned (${executedChecksums.size} skipped)" }
        logger.info { "" }
        return prunedScripts
    }
}