package datamaintain.core.report

import ch.qos.logback.classic.Logger
import datamaintain.core.step.Step
import datamaintain.core.step.check.rules.implementations.AlwaysSucceedCheck
import datamaintain.test.ScriptWithContentWithFixedChecksum
import datamaintain.test.TestAppender
import datamaintain.test.buildReportExecutedScript
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.map

internal class ReportTest {
    private val logger = LoggerFactory.getLogger("datamaintain.core.report.Report") as Logger
    private val testAppender = TestAppender()

    @BeforeEach
    fun setupLogger() {
        logger.addAppender(testAppender)
        testAppender.start()
    }

    /// Report fixture, containining everything
    private fun reportFixture(): Report =
     Report(
             scannedScripts = listOf(ScriptWithContentWithFixedChecksum("Scanned Script 1", "Scan Identifier 1", "CHKSCAN1")),
             filteredScripts = listOf(ScriptWithContentWithFixedChecksum("Filtered Script 1", "Filter Identifier 1", "CHKFIL2")),
             prunedScripts = listOf(ScriptWithContentWithFixedChecksum("Pruned Script 1", "Prune Identifier 1", "CHKPRUN3")),
             executedScripts = listOf(
                    buildReportExecutedScript(
                            "script1",
                            "porcelainName1"
                    ), buildReportExecutedScript(
                    "script2",
                    "porcelainName2"
                    )
             ),
             validatedCheckRules = listOf(AlwaysSucceedCheck())
    )

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

        @Test
        fun `should print only summary if neither verbose nor porcelain are set`() {
            // Given
            val report = reportFixture()

            // When
            report.print(verbose = false, porcelain = false)

            // Then
            expectThat(testAppender.events).map { it.message }.containsExactly(listOf(
                    "Summary => ",
                    "- 1 files scanned",
                    "- 1 files filtered",
                    "- 1 files pruned",
                    "- 1 check rules validated",
                    "- 2 files executed",
                    " -> script1",
                    " -> script2"
            ))
        }


        @Test
        fun `should print details if verbose is set`() {
            // Given
            val report = reportFixture()

            // When
            report.print(verbose = true, porcelain = false)

            // Then
            expectThat(testAppender.events).map { it.message }.containsExactly(listOf(
                    "Summary => ",
                    "- 1 files scanned",
                    " -> Scanned Script 1",
                    "- 1 files filtered",
                    " -> Filtered Script 1",
                    "- 1 files pruned",
                    " -> Pruned Script 1",
                    "- 1 check rules validated",
                    " -> AlwaysSucceed",
                    "- 2 files executed",
                    " -> script1",
                    " -> script2"
            ))
        }

        @Test
        fun `should print only up to max step`() {
            // Given
            val report = reportFixture()

            // When
            report.print(verbose = true, porcelain = false, maxStepToShow = Step.PRUNE)

            // Then
            expectThat(testAppender.events).map { it.message }.containsExactly(listOf(
                    "Summary => ",
                    "- 1 files scanned",
                    " -> Scanned Script 1",
                    "- 1 files filtered",
                    " -> Filtered Script 1",
                    "- 1 files pruned",
                    " -> Pruned Script 1"
            ))
        }

    }

    @AfterEach
    fun stopTestAppender() {
        testAppender.stop()
        testAppender.clearEvents()
    }
}

