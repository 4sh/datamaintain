package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.ScriptAction
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Pruner(private val context: Context) {
    fun prune(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        logger.info { "Prune scripts..." }

        val listExecutedScripts = context.dbDriver.listExecutedScripts()

        val executedChecksums = listExecutedScripts
                .map { executedScript -> executedScript.checksum }
                .toList()

        var prunedScripts = scripts
                .filterNot { script -> doesScriptAlreadyExecuted(script, executedChecksums) }

        if (context.config.overrideExecutedScripts) {
            val executedNames = listExecutedScripts
                    .map { executedScript -> executedScript.fullName() }
                    .toList()

            scripts.onEach { script -> if (executedNames.contains(script.fullName())) {
                script.action = ScriptAction.OVERRIDE_EXECUTED
            }}
        }

        prunedScripts = prunedScripts.onEach { context.reportBuilder.addPrunedScript(it) }

        logger.info { "${prunedScripts.size} scripts pruned (${executedChecksums.size} skipped)" }
        logger.info { "" }

        return prunedScripts
    }

    private fun doesScriptAlreadyExecuted(script: ScriptWithContent, executedChecksums: List<String>): Boolean {
        val skipped = executedChecksums.contains(script.checksum) &&
                script.tags.intersect(context.config.tagsToPlayAgain).isEmpty()
        if (context.config.verbose && skipped) {
            logger.info {
                "${script.name} is skipped because it was already executed " +
                        "and it does not have a tag to play again."
            }
        }
        return skipped
    }
}