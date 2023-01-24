package datamaintain.domain.report

typealias ExecutionId = Int

interface IExecutionWorkflowMessagesSender {
    fun startExecution(): ExecutionId
    fun sendReport(executionId: ExecutionId, report: Report)
}