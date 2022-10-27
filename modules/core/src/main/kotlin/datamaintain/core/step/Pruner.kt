package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.script.ScriptAction
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Pruner(private val context: Context) {
    private val prunerConfig
        get() = context.config.pruner

    fun prune(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        if (!context.config.logs.porcelain) { logger.info { "Prune scripts..." } }
        try {
            val listExecutedScripts = context.dbDriver.listExecutedScripts()
    
            val executedChecksums = listExecutedScripts
                    .map { executedScript -> executedScript.checksum }
                    .toList()
    
            var prunedScripts = scripts
                    .filterNot { script -> doesScriptAlreadyExecuted(script, executedChecksums) }
    
            if (context.config.executor.overrideExecutedScripts) {
                val executedNames = listExecutedScripts
                        .map { executedScript -> executedScript.fullName() }
                        .toList()
    
                scripts.onEach { script -> if (executedNames.contains(script.fullName())) {
                    script.action = ScriptAction.OVERRIDE_EXECUTED
                }}
            }
    
            prunedScripts = prunedScripts.onEach { context.reportBuilder.addPrunedScript(it) }

            if (!context.config.logs.porcelain) {
                logger.info { "${prunedScripts.size} scripts pruned (${executedChecksums.size} skipped)" }
                logger.info { "" }
            }
    
            return prunedScripts
        } catch (datamaintainException: DatamaintainBaseException) {
            throw DatamaintainException(
                datamaintainException.message,
                Step.PRUNE,
                context.reportBuilder,
                datamaintainException.resolutionMessage
            )
        }
    }

    private fun doesScriptAlreadyExecuted(script: ScriptWithContent, executedChecksums: List<String>): Boolean {
        val skipped = executedChecksums.contains(script.checksum) &&
                script.tags.intersect(prunerConfig.tagsToPlayAgain).isEmpty()
        if (context.config.logs.verbose && skipped && !context.config.logs.porcelain) {
            logger.info {
                "${script.name} is skipped because it was already executed " +
                        "and it does not have a tag to play again."
            }
        }
        return skipped
    }
}
