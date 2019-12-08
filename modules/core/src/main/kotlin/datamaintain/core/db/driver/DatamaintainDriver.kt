package datamaintain.core.db.driver

import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.report.ExecutionLineReport

interface DatamaintainDriver {

    fun executeScript(script: ScriptWithContent): ExecutionLineReport

    fun listExecutedScripts(): Sequence<Script>

    fun markAsExecuted(script: Script)
}
