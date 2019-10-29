package datamaintain.db.drivers

import datamaintain.Script
import datamaintain.ScriptWithContent
import datamaintain.report.ExecutionLineReport

import datamaintain.report.ScriptLineReport

interface DatamaintainDriver {

    fun executeScript(script: ScriptWithContent): ExecutionLineReport

    fun listExecutedScripts(): Sequence<Script>

    fun markAsExecuted(script: Script)
}
