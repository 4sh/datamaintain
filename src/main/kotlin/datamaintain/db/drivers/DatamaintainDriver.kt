package datamaintain.db.drivers

import datamaintain.Script
import datamaintain.ScriptWithContent

import datamaintain.report.ScriptExecutionReport

interface DatamaintainDriver {

    fun executeScript(script: ScriptWithContent): ScriptExecutionReport

    fun listExecutedScripts(): Sequence<Script>

    fun markAsExecuted(script: Script)
}
