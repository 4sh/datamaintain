package datamaintain.db.drivers

import datamaintain.Script
import datamaintain.ScriptWithContent

interface DatamaintainDriver {

    fun executeScript(script: ScriptWithContent): ScriptExecutionReport

    fun listExecutedScripts(): Sequence<Script>

    fun markAsExecuted(script: Script)
}

class ScriptExecutionReport
