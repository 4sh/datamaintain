package datamaintain.core.report

import ch.qos.logback.classic.Logger
import datamaintain.test.TestAppender
import datamaintain.test.buildReportExecutedScript
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import strikt.api.expectThat
import strikt.assertions.isEqualTo


internal class ReportTest {
    private val logger = LoggerFactory.getLogger("datamaintain.core.report.Report") as Logger
    private val testAppender = TestAppender()

    @BeforeEach
    fun setupLogger() {
        logger.addAppender(testAppender)
        testAppender.start()
    }

    @Nested
    inner class ExecuteLogs {
        @Test
        fun `should display relative paths when porcelain is true`() {
            // Given
            val report = Report(
                executedScripts = listOf(
                    buildReportExecutedScript(
                        "script1",
                        "porcelainName1"
                    ), buildReportExecutedScript(
                        "script2",
                        "porcelainName2"
                    )
                )
            )

            // When
            report.print(verbose = true, porcelain = true)

            // Then
            expectThat(testAppender.events) {
                get { get(0).message }.isEqualTo(
                    "porcelainName1"
                )
                get { get(1).message }.isEqualTo(
                    "porcelainName2"
                )
            }
        }

        @Test
        fun `should not display relative paths when porcelain name is null`() {
            // Given
            val report = Report(
                executedScripts = listOf(
                    buildReportExecutedScript(
                        "script1",
                        null
                    ), buildReportExecutedScript(
                        "script2",
                        null
                    )
                )
            )

            // When
            report.print(verbose = true)

            // Then
            expectThat(testAppender.events) {
                get { get(6).message }.isEqualTo(
                    " -> script1"
                )
                get { get(7).message }.isEqualTo(
                    " -> script2"
                )
            }
        }
    }

    @AfterEach
    fun stopTestAppender() {
        testAppender.stop()
        testAppender.clearEvents()
    }
}
