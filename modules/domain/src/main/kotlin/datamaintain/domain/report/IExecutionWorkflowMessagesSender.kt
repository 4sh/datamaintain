package datamaintain.domain.report

import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ScriptWithContent
import java.util.*

typealias ExecutionId = UUID

interface IExecutionWorkflowMessagesSender {
    fun startExecution(): ExecutionId?
    fun sendReport(executionId: ExecutionId, report: Report)
    fun startScriptExecution(executionId: ExecutionId, script: ScriptWithContent, orderIndex: Int)
    fun stopScriptExecution(executionId: ExecutionId, executedScript: ExecutedScript)
}