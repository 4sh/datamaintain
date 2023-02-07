package datamaintain.monitoring

import ExecutionStartResponse
import MonitoringReport
import ScriptExecutionStart
import datamaintain.domain.report.ExecutionId
import datamaintain.domain.report.IExecutionWorkflowMessagesSender
import datamaintain.domain.report.Report
import datamaintain.domain.script.ScriptWithContent
import org.http4k.client.Java8HttpClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Jackson.auto
import java.time.Clock
import java.time.Instant

class ExecutionWorkflowMessagesSender(baseUrl: String, private val clock: Clock) : IExecutionWorkflowMessagesSender {
    private val httpClient = Java8HttpClient()
    private val executionApiBaseUrl = "$baseUrl/public/executions"

    override fun startExecution(): ExecutionId =
        httpClient(Request(Method.POST, "$executionApiBaseUrl/start"))
            .let(executionStartResponse)
            .executionId

    override fun sendReport(executionId: ExecutionId, report: Report) {
        httpClient(Request(Method.PUT, "$executionApiBaseUrl/stop/$executionId").body(report.toMonitoringReport()))
    }

    override fun startScriptExecution(executionId: ExecutionId, script: ScriptWithContent) {
        httpClient(
            Request(Method.PUT, "$executionApiBaseUrl/$executionId/script/start")
                .body(script.toScriptExecutionStart(clock.instant()))
        )
    }

    companion object {
        val executionStartResponse = Body.auto<ExecutionStartResponse>().toLens()
        val scriptExecutionStart = Body.auto<ScriptExecutionStart>().toLens()
    }
}

fun Report.toMonitoringReport() =
    MonitoringReport(this.executedScripts.size)

fun ScriptWithContent.toScriptExecutionStart(startDate: Instant) =
    ScriptExecutionStart(
        name = this.name,
        checksum = this.checksum,
        content = this.content,
        startDate = startDate,
        tags = this.tags.map { it.name }
    )

fun <T : Any> Request.body(payload: T) =
    this.body(datamaintainJackson.asFormatString(payload))