package datamaintain.core.db.driver

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent

interface DatamaintainDriver {

    fun listExecutedScripts(): Sequence<ExecutedScript>

    fun executeScript(script: ScriptWithContent): ExecutedScript

    fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript
}
