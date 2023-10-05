package datamaintain.core.report

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
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
import strikt.assertions.isEqualTo
import strikt.assertions.map


internal class ReportTest {
    private val logger = LoggerFactory.getLogger("datamaintain.core.report.Report") as Logger
    private val loggerLevel: Level? = logger.level
    private val testAppender = TestAppender()

    @BeforeEach
    fun setupLogger() {
        logger.level = Level.INFO
        logger.addAppender(testAppender)
        testAppender.start()
    }

    @AfterEach
    fun afterEach() {
        logger.level = loggerLevel
    }

    /// builds an example report containining everything
    private fun buildReport(): Report =
     Report(
             scannedScripts = listOf(ScriptWithContentWithFixedChecksum("Scanned Script 1", "Scan Identifier 1", "CHKSCAN1")),
             filteredScripts = listOf(ScriptWithContentWithFixedChecksum("Filtered Script 1", "Filter Identifier 1", "CHKFIL2")),
             prunedScripts = listOf(ScriptWithContentWithFixedChecksum("Pruned Script 1", "Prune Identifier 1", "CHKPRUN3")),
             executedScripts = listOf(
                 buildReportExecutedScript("script1"),
                 buildReportExecutedScript("script2")
             ),
             validatedCheckRules = listOf(AlwaysSucceedCheck())
    )

    @Nested
    inner class ExecuteLogs {
        @Test
        fun `should print script name in trace`() {
            // Given
            val report = Report(
                executedScripts = listOf(
                    buildReportExecutedScript(
                        "script1",
                    ), buildReportExecutedScript(
                        "script2",
                    )
                )
            )
            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print()

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
        fun `should print only summary`() {
            // Given
            val report = buildReport()

            // When
            report.print()

            // Then
            expectThat(testAppender.events).map { it.message }.containsExactly(listOf(
                    "Summary => ",
                    "- 2 files executed",
                    " -> script1",
                    " -> script2"
            ))
        }

        @Test
        fun `should print only summary on debug`() {
            // Given
            val report = buildReport()

            // switch to DEBUG
            logger.level = Level.DEBUG

            // When
            report.print()

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
        fun `should print only summary on trace`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print()

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
        fun `should print details if verbose is set`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print()

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
        fun `should print only up to SCAN step if configured`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print(maxStepToShow = Step.SCAN)

            // Then
            expectThat(testAppender.events).map { it.message }.containsExactly(listOf(
                    "Summary => ",
                    "- 1 files scanned",
                    " -> Scanned Script 1"
            ))
        }

        @Test
        fun `should print only up to FILTER step if configured`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print(maxStepToShow = Step.FILTER)

            // Then
            expectThat(testAppender.events).map { it.message }.containsExactly(listOf(
                    "Summary => ",
                    "- 1 files scanned",
                    " -> Scanned Script 1",
                    "- 1 files filtered",
                    " -> Filtered Script 1"
            ))
        }

        @Test
        fun `should print only up to SORT step if configured`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print(maxStepToShow = Step.SORT)

            // Then
            expectThat(testAppender.events).map { it.message }.containsExactly(listOf(
                    "Summary => ",
                    "- 1 files scanned",
                    " -> Scanned Script 1",
                    "- 1 files filtered",
                    " -> Filtered Script 1"
            ))
        }

        @Test
        fun `should print only up to PRUNE step if configured`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print(maxStepToShow = Step.PRUNE)

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

        @Test
        fun `should print only up to CHECK step if configured`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print(maxStepToShow = Step.CHECK)

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
                    " -> AlwaysSucceed"
            ))
        }

        @Test
        fun `should print only up to EXECUTE step if configured`() {
            // Given
            val report = buildReport()

            // switch to TRACE
            logger.level = Level.TRACE

            // When
            report.print(maxStepToShow = Step.EXECUTE)

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
    }

    @AfterEach
    fun stopTestAppender() {
        testAppender.stop()
        testAppender.clearEvents()
    }
}
