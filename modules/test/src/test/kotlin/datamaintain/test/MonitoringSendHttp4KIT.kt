package datamaintain.test

import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.config.MonitoringConfiguration
import datamaintain.domain.report.ExecutionId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

/**
 * When monitoring configuration is given an url for monitoring, Executor should send information about
 * the current batch to the remote monitoring server
 */
class MonitoringSendHttp4KIT: AbstractMonitoringSendWithHttpTest() {
    private val executionId: ExecutionId = 42

    @Nested
    inner class MonitoringIsReachable {
        @BeforeEach
        fun setupStartAnswer() {
            mockServerClient.`when`(
                request()
                    .withMethod("POST")
                    .withPath("/public/executions/start")
            ).respond(response().withBody("{\"executionId\": $executionId}"))
        }

        @Test
        fun should_send_start_message_to_monitoring() {
            // When
            buildDatamaintainWithMonitoringConfiguration().updateDatabase()

            // Then
            mockServerClient.verify(request().withPath("/public/executions/start"))
        }
    }

    private fun buildDatamaintainWithMonitoringConfiguration() = Datamaintain(
        DatamaintainConfig(
            driverConfig = FakeDriverConfig(),
            monitoringConfiguration = MonitoringConfiguration(mockServerUrl)
        )
    )
}