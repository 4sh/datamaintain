package datamaintain.cli.app.update.db

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import datamaintain.cli.app.BaseCliTest
import datamaintain.core.script.ScriptAction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class MarkOneScriptAsExecutedTest : BaseCliTest() {
    @Nested
    inner class ConfigurationBuild {
        @Nested
        inner class GenericConfiguration {
            @Test
            fun `should build config with absolute path`() {
                // Given
                val path = "/myPath"

                val markScriptAsExecutedArguments =
                    listOf(
                        "--path", path
                    )

                // When
                runMarkScriptAsExecuted(markScriptAsExecutedArguments = markScriptAsExecutedArguments)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.scanner.path).isEqualTo(Paths.get(path))
            }

            @Nested
            inner class Verbose {
                private lateinit var datamaintainLogger: Logger
                private lateinit var datamaintainLoggerLevel: Level

                @BeforeEach
                fun beforeEach() {
                    if (!::datamaintainLogger.isInitialized) {
                        val context = LoggerFactory.getILoggerFactory() as LoggerContext
                        datamaintainLogger = context.getLogger("datamaintain")
                        datamaintainLoggerLevel = datamaintainLogger.level
                    }
                }

                @AfterEach
                fun afterEach() {
                    datamaintainLogger.level = datamaintainLoggerLevel
                }

                @Test
                fun `should build config with verbose set to true`() {
                    // Given
                    val baseArguments = listOf("--verbose")

                    // When
                    runMarkScriptAsExecuted(baseArguments)

                    // Then
                    expectThat(datamaintainLogger.level).isEqualTo(Level.DEBUG)
                }

                @Test
                fun `should build config with verbose set to false`() {
                    // Given

                    // When
                    runMarkScriptAsExecuted()

                    // Then
                    expectThat(datamaintainLogger.level).isEqualTo(Level.INFO)
                }

                @Test
                fun `should build config with trace set to true`() {
                    // Given
                    val baseArguments = listOf("-vv")

                    // When
                    runMarkScriptAsExecuted(baseArguments)

                    // Then
                    expectThat(datamaintainLogger.level).isEqualTo(Level.TRACE)
                }

                @Test
                fun `should build config with trace set to true even if verbose is set`() {
                    // Given
                    val baseArguments = listOf("--verbose", "-vv")

                    // When
                    runMarkScriptAsExecuted(baseArguments)

                    // Then
                    expectThat(datamaintainLogger.level).isEqualTo(Level.TRACE)
                }
            }

            @Test
            fun `should build config with mark as executed for script action`() {
                // Given

                // When
                runMarkScriptAsExecuted()

                // Then
                expectThat(configWrapper.datamaintainConfig!!.executor.defaultScriptAction).isEqualTo(ScriptAction.MARK_AS_EXECUTED)
            }
        }
    }

    private fun runMarkScriptAsExecuted(baseArguments: List<String> = listOf(), markScriptAsExecutedArguments: List<String> = listOf()) {
        val base = baseArguments + listOf(
            "--db-type", "mongo",
            "--db-uri", "mongo-uri"
        )

        runAppWithMarkOneScriptAsExecuted(base, markScriptAsExecutedArguments)
    }
}

