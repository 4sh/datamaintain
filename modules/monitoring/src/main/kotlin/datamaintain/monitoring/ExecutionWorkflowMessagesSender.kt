package datamaintain.monitoring

import ExecutionStartResponse
import MonitoringReport
import datamaintain.domain.report.ExecutionId
import datamaintain.domain.report.IExecutionWorkflowMessagesSender
import datamaintain.domain.report.Report
import org.http4k.client.Java8HttpClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Jackson.auto

class ExecutionWorkflowMessagesSender : IExecutionWorkflowMessagesSender {
    private val httpClient = Java8HttpClient()
    private val executionApiBaseUrl = "$baseUrl/public/executions"

    override fun startExecution(): ExecutionId =
        httpClient(Request(Method.POST, "$executionApiBaseUrl/start"))
            .let(executionStartResponse)
            .executionId

    override fun sendReport(executionId: ExecutionId, report: Report) {
        httpClient(Request(Method.PUT, "$executionApiBaseUrl/stop/$executionId").body(report.toMonitoringReport()))
    }

    companion object {
        val executionStartResponse = Body.auto<ExecutionStartResponse>().toLens()
    }
}

fun Report.toMonitoringReport() =
    MonitoringReport(this.executedScripts.size)

fun <T : Any> Request.body(payload: T) =
    this.body(datamaintainJackson.asFormatString(payload))