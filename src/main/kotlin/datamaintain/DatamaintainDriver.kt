package datamaintain

interface DatamaintainDriver {

    fun executeScript(script: ScriptWithContent): ScriptExecutionReport

    fun listExecutedScripts(): List<Script>

    fun markAsExecuted(script: Script)
}

class ScriptExecutionReport
