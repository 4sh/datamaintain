package datamaintain.cli.app.update.db

import datamaintain.cli.app.BaseCliTest
import datamaintain.domain.script.ScriptAction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
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
                runMarkScriptAsExecuted(markScriptAsExecutedArguments)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.path).isEqualTo(Paths.get(path))
            }

            @Nested
            inner class Verbose {
                @Test
                fun `should build config with verbose set to true`() {
                    // Given
                    val markScriptAsExecutedArguments = listOf("--verbose")

                    // When
                    runMarkScriptAsExecuted(markScriptAsExecutedArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.verbose).isTrue()
                }

                @Test
                fun `should build config with verbose set to false`() {
                    // Given

                    // When
                    runMarkScriptAsExecuted()

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.verbose).isFalse()
                }
            }

            @Test
            fun `should build config with mark as executed for script action`() {
                // Given

                // When
                runMarkScriptAsExecuted()

                // Then
                expectThat(configWrapper.datamaintainConfig!!.defaultScriptAction).isEqualTo(ScriptAction.MARK_AS_EXECUTED)
            }
        }
    }

    private fun runMarkScriptAsExecuted(markScriptAsExecutedArguments: List<String> = listOf()) {
        runAppWithMarkOneScriptAsExecuted(
            listOf(
                    "--db-type", "mongo",
                    "--db-uri", "mongo-uri"
            ), markScriptAsExecutedArguments
        )
    }
}