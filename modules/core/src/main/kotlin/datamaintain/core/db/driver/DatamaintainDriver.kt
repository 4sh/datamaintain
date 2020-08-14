package datamaintain.core.db.driver

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.executor.Execution

interface DatamaintainDriver {

    fun listExecutedScripts(): Sequence<ExecutedScript>

    fun executeScript(script: ScriptWithContent): Execution

    fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript
}
