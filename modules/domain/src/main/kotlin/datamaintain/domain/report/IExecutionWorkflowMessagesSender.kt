package datamaintain.domain.report

import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ScriptWithContent
import java.util.*

typealias ExecutionId = UUID
typealias ScriptExecutionId = UUID

interface IExecutionWorkflowMessagesSender {
    fun startExecution(): ExecutionId?
    fun startScriptExecution(executionId: ExecutionId, script: ScriptWithContent, orderIndex: Int): ScriptExecutionId?
    fun stopScriptExecution(scriptExecutionId: ScriptExecutionId, executedScript: ExecutedScript)
    fun sendSuccessReport(executionId: ExecutionId)
    fun sendFailReport(executionId: ExecutionId)
}