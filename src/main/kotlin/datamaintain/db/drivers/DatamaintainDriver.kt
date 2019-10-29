package datamaintain.db.drivers

import datamaintain.Script
import datamaintain.ScriptWithContent

import datamaintain.report.ScriptLineReport

interface DatamaintainDriver {

    fun executeScript(script: ScriptWithContent): ScriptLineReport

    fun listExecutedScripts(): Sequence<Script>

    fun markAsExecuted(script: Script)
}
