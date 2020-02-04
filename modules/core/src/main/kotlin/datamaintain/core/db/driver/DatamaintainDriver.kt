package datamaintain.core.db.driver

import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.ExecutedScript

interface DatamaintainDriver {

    fun listExecutedScripts(): Sequence<Script>

    fun executeScript(script: ScriptWithContent): ExecutedScript

    fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript
}
