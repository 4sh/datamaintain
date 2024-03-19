package datamaintain.monitoring

import datamaintain.domain.report.ExecutionId
import datamaintain.domain.report.IExecutionWorkflowMessagesSender
import datamaintain.domain.report.ScriptExecutionId
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ExecutionStatus
import datamaintain.domain.script.ScriptWithContent
import datamaintain.monitoring.api.execution.report.api.*
import org.http4k.client.JavaHttpClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import java.time.Clock
import java.time.Instant

class Http4KExecutionWorkflowMessagesSender(
    baseUrl: String,
    private val moduleEnvironmentToken: String,
    private val clock: Clock
) : IExecutionWorkflowMessagesSender {
    private val httpClient = JavaHttpClient()
    private val executionApiBaseUrl = "$baseUrl/v1/executions"

    override fun startExecution(): ExecutionId? =
        httpClient(
            Request(Method.POST, "$executionApiBaseUrl/start")
                .body(ExecutionStart(clock.instant(), moduleEnvironmentToken))
        ).takeIf { it.status == Status.OK }
            ?.let(executionStartResponse)
            ?.executionId

    override fun sendSuccessReport(executionId: ExecutionId) {
        sendReport(
            executionId = executionId,
            success = true
        )
    }

    override fun sendFailReport(executionId: ExecutionId) {
        sendReport(
            executionId = executionId,
            success = false
        )
    }

    private fun sendReport(executionId: ExecutionId, success: Boolean) {
        httpClient(
            Request(Method.PUT, "$executionApiBaseUrl/stop/$executionId")
                .body(
                    ExecutionStopRequest(
                        endDate = clock.instant(),
                        batchEndStatus = if (success) {
                            BatchEndStatus.COMPLETED
                        } else {
                            BatchEndStatus.ERROR
                        }
                    )
                )
        )
    }

    override fun startScriptExecution(
        executionId: ExecutionId,
        script: ScriptWithContent,
        orderIndex: Int
    ): ScriptExecutionId? =
        httpClient(
            Request(Method.POST, "$executionApiBaseUrl/$executionId/scripts/start")
                .body(script.toScriptExecutionStart(clock.instant(), orderIndex))
        ).takeIf { it.status == Status.OK }
            ?.let(scriptExecutionStartResponse)
            ?.scriptExecutionId


    override fun stopScriptExecution(scriptExecutionId: ScriptExecutionId, executedScript: ExecutedScript) {
        httpClient(
            Request(Method.PUT, "$executionApiBaseUrl/scripts/$scriptExecutionId/stop")
                .body(executedScript.toScriptExecutionStop(clock.instant()))
        )
    }

    companion object {
        val executionStartResponse = Body.auto<ExecutionStartResponse>().toLens()
        val scriptExecutionStartResponse = Body.auto<ScriptExecutionStartResponse>().toLens()
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
        .header("Content-Type", "application/json")