package datamaintain.monitoring

import datamaintain.monitoring.api.execution.report.api.ExecutionStartResponse
import datamaintain.monitoring.api.execution.report.api.MonitoringReport
import datamaintain.monitoring.api.execution.report.api.ScriptExecutionStart
import datamaintain.monitoring.api.execution.report.api.ScriptExecutionStop
import datamaintain.domain.report.ExecutionId
import datamaintain.domain.report.IExecutionWorkflowMessagesSender
import datamaintain.domain.report.Report
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ExecutionStatus
import datamaintain.domain.script.ScriptWithContent
import org.http4k.client.Java8HttpClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import java.time.Clock
import java.time.Instant

class Http4KExecutionWorkflowMessagesSender(baseUrl: String, private val clock: Clock) : IExecutionWorkflowMessagesSender {
    private val httpClient = Java8HttpClient()
    private val executionApiBaseUrl = "$baseUrl/v1/executions"

    override fun startExecution(): ExecutionId? =
        httpClient(Request(Method.POST, "$executionApiBaseUrl/start"))
            .takeIf { it.status == Status.OK }
            ?.let(executionStartResponse)
            ?.executionId

    override fun sendReport(executionId: ExecutionId, report: Report) {
        httpClient(Request(Method.PUT, "$executionApiBaseUrl/stop/$executionId").body(report.toMonitoringReport()))
    }

    override fun startScriptExecution(executionId: ExecutionId, script: ScriptWithContent, orderIndex: Int) {
        httpClient(
            Request(Method.PUT, "$executionApiBaseUrl/$executionId/script/start")
                .body(script.toScriptExecutionStart(clock.instant(), orderIndex))
        )
    }

    override fun stopScriptExecution(executionId: ExecutionId, executedScript: ExecutedScript) {
        httpClient(
            Request(Method.PUT, "$executionApiBaseUrl/$executionId/script/stop")
                .body(executedScript.toScriptExecutionStop(clock.instant()))
        )
    }

    companion object {
        val executionStartResponse = Body.auto<ExecutionStartResponse>().toLens()
        val scriptExecutionStart = Body.auto<ScriptExecutionStart>().toLens()
    }
}

private fun ExecutedScript.toScriptExecutionStop(endDate: Instant): ScriptExecutionStop = ScriptExecutionStop(
    executionStatus = executionStatus.toMonitoringExecutionStatus(),
    executionOutput = executionOutput,
    executionEndDate = endDate
)

private fun ExecutionStatus.toMonitoringExecutionStatus(): datamaintain.monitoring.api.execution.report.api.ExecutionStatus =
    when(this) {
        ExecutionStatus.OK -> datamaintain.monitoring.api.execution.report.api.ExecutionStatus.OK
        ExecutionStatus.KO -> datamaintain.monitoring.api.execution.report.api.ExecutionStatus.KO
    }


fun Report.toMonitoringReport() =
    MonitoringReport(this.executedScripts.size)

fun ScriptWithContent.toScriptExecutionStart(startDate: Instant, orderIndex: Int) =
    ScriptExecutionStart(
        name = this.name,
        content = this.content,
        startDate = startDate,
        tags = this.tags.map { it.name },
        executionOrderIndex = orderIndex
    )

fun <T : Any> Request.body(payload: T) =
    this.body(datamaintainJackson.asFormatString(payload))