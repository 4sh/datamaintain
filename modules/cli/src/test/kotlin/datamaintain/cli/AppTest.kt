package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.step.check.rules.implementations.SameScriptsAsExecutedCheck
import org.junit.jupiter.api.Nested
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class AppTest {
    data class ConfigWrapper(var datamaintainConfig: DatamaintainConfig? = null)

    private val configWrapper = ConfigWrapper()

    private fun runner(config: DatamaintainConfig) {
        configWrapper.datamaintainConfig = config
    }

    @Nested
    inner class UpdateDb {
        @Test
        fun `should build config with path`() {
            // Given
            val path = "myPath"

            val argv = updateDbMinimumArguments().plus(listOf(
                    "--path", path
            ))

            // When
            runUpdateDb(argv)

            // Then
            expectThat(configWrapper.datamaintainConfig!!.path).get { Paths.get(path) }
        }

        @Test
        fun `should build config with identifier regex`() {
            // Given
            val identifierRegex = "myIdentifierRegex"

            val argv = updateDbMinimumArguments().plus(listOf(
                    "--identifier-regex", identifierRegex
            ))

            // When
            runUpdateDb(argv)

            // Then
            expectThat(configWrapper.datamaintainConfig!!.identifierRegex).get { identifierRegex }
        }

        @Nested
        inner class Rules {
            @Test
            fun `should build config with one rule`() {
                // Given
                val argv = updateDbMinimumArguments().plus(listOf(
                        "--rule", SameScriptsAsExecutedCheck.NAME
                ))

                // When
                runUpdateDb(argv)

                // Then
                expectThat(configWrapper) {
                    get { datamaintainConfig }.isNotNull()
                }
                expectThat(configWrapper.datamaintainConfig!!.checkRules.toList()) {
                    hasSize(1)
                    first().isEqualTo(SameScriptsAsExecutedCheck.NAME)
                }
            }

            @Test
            fun `should build config with 2 rules`() {
                // Given
                val argv = updateDbMinimumArguments().plus(listOf(
                        "--rule", SameScriptsAsExecutedCheck.NAME,
                        "--rule", SameScriptsAsExecutedCheck.NAME
                ))

                // When
                runUpdateDb(argv)

                // Then
                expectThat(configWrapper) {
                    get { datamaintainConfig }.isNotNull()
                }
                expectThat(configWrapper.datamaintainConfig!!.checkRules.toList()) {
                    hasSize(2)
                    first().isEqualTo(SameScriptsAsExecutedCheck.NAME)
                    last().isEqualTo(SameScriptsAsExecutedCheck.NAME)
                }
            }
        }

        private fun runUpdateDb(argv: List<String>) {
            App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)
        }

        private fun updateDbMinimumArguments(): List<String> {
            return listOf(
                    "--mongo-uri", "mongo-uri",
                    "update-db"
            )
        }
    }
}