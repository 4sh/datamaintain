package datamaintain.test

import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.config.MonitoringConfiguration
import datamaintain.domain.report.ExecutionId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import strikt.api.expectCatching
import strikt.assertions.isSuccess
import java.nio.file.Paths

/**
 * When monitoring configuration is given an url for monitoring, Executor should send information about
 * the current batch to the remote monitoring server
 */
class MonitoringSendHttp4KIT : AbstractMonitoringSendWithHttpTest() {
    @Nested
    inner class MonitoringIsUnreachable {
        @Test
        fun should_pursue_execution_when_start_does_not_answer() {
            expectCatching { buildDatamaintainWithMonitoringConfiguration().updateDatabase() }
                .isSuccess()
        }
    }

    @Nested
    inner class MonitoringIsReachable {
        @Test
        fun should_send_POST_start_message_to_monitoring_on_correct_URL() {
            // When
            setupMockStartAnswer()
            buildDatamaintainWithMonitoringConfiguration().updateDatabase()

            // Then
            mockServerClient.verify(request().withPath("/public/executions/start")
                .withMethod("POST"))
        }

        @Nested
        inner class ScriptExecutionMessages {
            @Test
            fun should_send_script_execution_start_with_execution_id() {
                // When
                val executionId = 12
                setupMockStartAnswer(executionId)
                buildDatamaintainWithMonitoringConfiguration("src/test/resources/integration/ok").updateDatabase()

                // Then
                mockServerClient.verify(request().withPath("/public/executions/$executionId/script/start").withMethod("PUT"))
            }
        }
    }

    private fun setupMockStartAnswer(executionId: ExecutionId = 42) {
        mockServerClient.`when`(
            request()
                .withMethod("POST")
                .withPath("/public/executions/start")
        ).respond(response().withBody("{\"executionId\": $executionId}"))
    }

    private fun buildDatamaintainWithMonitoringConfiguration(scriptsPath: String = "") = Datamaintain(
        DatamaintainConfig(
            path = Paths.get(scriptsPath),
            driverConfig = FakeDriverConfig(),
            monitoringConfiguration = MonitoringConfiguration(mockServerUrl)
        )
    )
}