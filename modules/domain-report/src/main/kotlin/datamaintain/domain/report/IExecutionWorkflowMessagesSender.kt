package datamaintain.domain.report

interface IExecutionWorkflowMessagesSender {
    fun sendReport(report: Report)
}