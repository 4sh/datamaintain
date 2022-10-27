package datamaintain.core.config

import datamaintain.core.script.ScriptAction
import datamaintain.core.step.executor.ExecutionMode
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class DatamaintainExecutorConfig @JvmOverloads constructor(
    val executionMode: ExecutionMode = defaultExecutionMode,
    val overrideExecutedScripts: Boolean = CoreConfigKey.PRUNE_OVERRIDE_UPDATED_SCRIPTS.default!!.toBoolean(),
    val defaultScriptAction: ScriptAction = defaultAction,
    val flags: List<String> = emptyList(),
) {
    fun log() {
        executionMode.let { logger.info { "- execution mode -> $it" } }
        overrideExecutedScripts.let { logger.info { "- allow override executed script -> $it" } }
        defaultScriptAction.let { logger.info { "- script action -> $it" } }
        flags.let { logger.info { "- flags -> $it" } }
    }

    companion object {
        val defaultExecutionMode = ExecutionMode.NORMAL
        val defaultAction = ScriptAction.RUN
    }
}
