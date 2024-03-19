package datamaintain.test

import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.config.DatamaintainMonitoringConfiguration
import datamaintain.core.config.DatamaintainScannerConfig
import datamaintain.core.script.TagMatcher
import datamaintain.domain.report.ExecutionId
import datamaintain.domain.script.Tag
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.StringBody.subString
import org.mockserver.verify.VerificationTimes
import strikt.api.expectCatching
import strikt.assertions.isSuccess
import java.nio.file.Paths
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * When monitoring configuration is given an url for monitoring, Executor should send information about
 * the current batch to the remote monitoring server
 */
class MonitoringSendHttp4KIT : AbstractMonitoringSendWithHttpTest() {
    @Nested
    inner class MonitoringIsUnreachable {
        @Test
        internal fun should_pursue_execution_when_start_does_not_answer() {
            expectCatching { buildDatamaintainWithMonitoringConfiguration().updateDatabase() }
                .isSuccess()
        }
    }

    @Nested
    inner class MonitoringIsReachable {
        @Test
        internal fun should_send_POST_start_message_to_monitoring_on_correct_URL() {
            // When
            setupMockBatchExecutionStartAnswer()
            buildDatamaintainWithMonitoringConfiguration().updateDatabase()

            // Then
            mockServerClient.verify(request().withPath("/v1/executions/start")
                .withMethod("POST"))
        }

        @Nested
        inner class ScriptExecutionStartMessage {
            @Test
            internal fun should_send_script_execution_start_with_execution_id() {
                // When
                val executionId = UUID.randomUUID()
                setupMockBatchExecutionStartAnswer(executionId)
                val scriptExecutionId = UUID.randomUUID()
                setupMockScriptExecutionStartAnswer(executionId = executionId, scriptExecutionId = scriptExecutionId)
                buildDatamaintainWithMonitoringConfiguration("src/test/resources/integration/ok").updateDatabase()

                // Then
                mockServerClient.verify(
                    request().withPath("/v1/executions/$executionId/scripts/start").withMethod("POST")
                )
            }

            @Test
            internal fun should_send_script_name_in_body() {
                checkStartMessageBodyContains("\"name\":\"01_file.js\"")
            }

            @Test
            internal fun should_send_script_content_in_body() {
                checkStartMessageBodyContains("\"content\":\"db.simple.insert({ find: \\\"1\\\", data: 'inserted'});\\n\\nprint(\\\"01 OK\\\");\"")
            }

            @Test
            internal fun should_send_script_start_date_in_body() {
                checkStartMessageBodyContains("\"startDate\":\"2023-03-21T11:17:33.337Z\"")
            }

            @Test
            internal fun should_send_script_execution_order_index_in_body() {
                checkStartMessageBodyContains("\"executionOrderIndex\":0")
            }

            @Test
            internal fun should_send_script_tags_in_body() {
                checkStartMessageBodyContains("\"tags\":[\"myTag\"]")
            }

            private fun checkStartMessageBodyContains(subStringExpectedInBody: String) {
                val executionId = UUID.randomUUID()
                setupMockBatchExecutionStartAnswer(executionId)
                val scriptExecutionId = UUID.randomUUID()
                setupMockScriptExecutionStartAnswer(executionId = executionId, scriptExecutionId = scriptExecutionId)
                buildDatamaintainWithMonitoringConfiguration(
                    scriptsPath = "src/test/resources/integration/ok",
                    tagsMatchers = setOf(TagMatcher(Tag("myTag"), listOf("src/test/resources/integration/ok/*"))),
                    clock = Clock.fixed(Instant.parse("2023-03-21T11:17:33.337Z"), ZoneId.systemDefault())
                ).updateDatabase()

                mockServerClient.verify(request()
                    .withPath("/v1/executions/$executionId/scripts/start")
                    .withMethod("POST")
                    .withBody(subString(subStringExpectedInBody)), VerificationTimes.atLeast(1)
                )
            }
        }

        @Nested
        inner class ScriptExecutionStopMessage {
            @Test
            internal fun should_send_script_execution_stop_with_execution_id() {
                // When
                val executionId = UUID.randomUUID()
                setupMockBatchExecutionStartAnswer(executionId)
                val scriptExecutionId = UUID.randomUUID()
                setupMockScriptExecutionStartAnswer(executionId = executionId, scriptExecutionId = scriptExecutionId)
                buildDatamaintainWithMonitoringConfiguration("src/test/resources/integration/ok").updateDatabase()

                // Then
                mockServerClient.verify(
                    request().withPath("/v1/executions/scripts/$scriptExecutionId/stop").withMethod("PUT"),
                    VerificationTimes.atLeast(1)
                )
            }

            @Test
            internal fun should_send_script_execution_status_in_body() {
                checkStopMessageBodyContains("\"executionStatus\":\"OK\"")
            }

            @Test
            internal fun should_send_script_execution_output_in_body() {
                checkStopMessageBodyContains("\"executionOutput\":\"$fakeDriverScriptExecutionOutput\"")
            }

            @Test
            internal fun should_send_script_execution_end_date_in_body() {
                checkStopMessageBodyContains("\"executionEndDate\":\"2023-03-21T11:17:33.337Z\"")
            }

            private fun checkStopMessageBodyContains(subStringExpectedInBody: String) {
                val executionId = UUID.randomUUID()
                setupMockBatchExecutionStartAnswer(executionId)
                val scriptExecutionId = UUID.randomUUID()
                setupMockScriptExecutionStartAnswer(executionId = executionId, scriptExecutionId = scriptExecutionId)
                buildDatamaintainWithMonitoringConfiguration(
                    scriptsPath = "src/test/resources/integration/ok",
                    clock = Clock.fixed(Instant.parse("2023-03-21T11:17:33.337Z"), ZoneId.systemDefault())
                ).updateDatabase()

                mockServerClient.verify(request()
                    .withPath("/v1/executions/scripts/$scriptExecutionId/stop")
                    .withMethod("PUT")
                    .withBody(subString(subStringExpectedInBody)),
                    VerificationTimes.atLeast(1)
                )
            }
        }

        @Nested
        inner class ExecutionStopMessage {
            @Test
            internal fun should_send_script_execution_stop_with_execution_id() {
                // When
                val executionId = UUID.randomUUID()
                setupMockBatchExecutionStartAnswer(executionId)
                buildDatamaintainWithMonitoringConfiguration("src/test/resources/integration/ok").updateDatabase()

                // Then
                mockServerClient.verify(request().withPath("/v1/executions/stop/$executionId").withMethod("PUT"))
            }

            @Test
            internal fun should_send_end_date() {
                checkExecutionStopMessageBodyContains("\"endDate\":\"2024-03-19T18:28:46.534228Z\"")
            }

            @Test
            internal fun should_send_batch_end_status() {
                checkExecutionStopMessageBodyContains("\"batchEndStatus\":\"COMPLETED\"")
            }

            private fun checkExecutionStopMessageBodyContains(subStringExpectedInBody: String) {
                val executionId = UUID.randomUUID()
                setupMockBatchExecutionStartAnswer(executionId)
                buildDatamaintainWithMonitoringConfiguration(
                    scriptsPath = "src/test/resources/integration/ok",
                    clock = Clock.fixed(Instant.parse("2024-03-19T18:28:46.534228Z"), ZoneId.systemDefault())
                ).updateDatabase()

                mockServerClient.verify(request()
                    .withPath("/v1/executions/stop/$executionId")
                    .withMethod("PUT")
                    .withBody(subString(subStringExpectedInBody)))
            }
        }
    }

    private fun setupMockScriptExecutionStartAnswer(executionId: UUID, scriptExecutionId: UUID) {
        mockServerClient.`when`(
            request()
                .withMethod("POST")
                .withPath("/v1/executions/${executionId}/scripts/start")
        ).respond(response().withBody("{\"scriptExecutionId\": \"$scriptExecutionId\"}"))
    }

    private fun setupMockBatchExecutionStartAnswer(executionId: ExecutionId = UUID.randomUUID()) {
        mockServerClient.`when`(
            request()
                .withMethod("POST")
                .withPath("/v1/executions/start")
        ).respond(response().withBody("{\"executionId\": \"$executionId\"}"))
    }

    private fun buildDatamaintainWithMonitoringConfiguration(
        scriptsPath: String = "",
        tagsMatchers: Set<TagMatcher> = setOf(),
        clock: Clock = Clock.system(ZoneId.systemDefault()),
        moduleEnvironmentToken: String = "myToken"
    ) = Datamaintain(
        DatamaintainConfig(
            scanner = DatamaintainScannerConfig(
                path = Paths.get(scriptsPath),
                tagsMatchers = tagsMatchers
            ),
            driverConfig = FakeDriverConfig(),
            monitoringConfiguration = DatamaintainMonitoringConfiguration(mockServerUrl, moduleEnvironmentToken)
        ),
        clock
    )
}