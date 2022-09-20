package datamaintain.monitoring

import MonitoringReport
import datamaintain.domain.report.IExecutionWorkflowMessagesSender
import datamaintain.domain.report.Report
import org.http4k.client.Java8HttpClient
import org.http4k.core.Method
import org.http4k.core.Request

class ExecutionWorkflowMessagesSender : IExecutionWorkflowMessagesSender {
    private val httpClient = Java8HttpClient()
    private val executionApiBaseUrl = "$baseUrl/public/executions"

    override fun sendReport(report: Report) {
        httpClient(Request(Method.POST, "$executionApiBaseUrl/report").body(report.toMonitoringReport()))
    }
}

fun Report.toMonitoringReport() =
    MonitoringReport(this.executedScripts.size)

fun <T : Any> Request.body(payload: T) =
    this.body(datamaintainJackson.asFormatString(payload))