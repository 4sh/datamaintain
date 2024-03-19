package datamaintain.domain.report

import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ScriptWithContent

typealias ExecutionId = Int

interface IExecutionWorkflowMessagesSender {
    fun startExecution(): ExecutionId?
    fun sendReport(executionId: ExecutionId, report: Report)
    fun startScriptExecution(executionId: ExecutionId, script: ScriptWithContent)
    fun stopScriptExecution(executionId: ExecutionId, executedScript: ExecutedScript)
}