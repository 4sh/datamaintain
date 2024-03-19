package datamaintain.cli.app.update.db

import datamaintain.cli.app.BaseCliTest
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.check.rules.implementations.SameScriptsAsExecutedCheck
import datamaintain.db.driver.mongo.MongoDriverConfig
import datamaintain.db.driver.mongo.MongoShell
import datamaintain.domain.script.ScriptAction
import datamaintain.domain.script.Tag
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class UpdateDbTest : BaseCliTest() {
    @Nested
    inner class ConfigurationBuild {
        @Nested
        inner class GenericConfiguration {
            @Test
            fun `should build config with absolute path`() {
                // Given
                val path = "/myPath"

                val updateDbArguments = listOf(
                    "--path", path
                )

                // When
                runUpdateDb(updateDbArguments)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.scanner.path).isEqualTo(Paths.get(path))
            }

            @Test
            fun `should build config with identifier regex`() {
                // Given
                val identifierRegex = "myIdentifierRegex"

                val updateDbArguments = listOf(
                        "--identifier-regex", identifierRegex
                    )

                // When
                runUpdateDb(updateDbArguments)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.scanner.identifierRegex.pattern).isEqualTo(identifierRegex)
            }

            @Nested
            inner class DatamaintainMonitoringConfiguration {
                // Check all the configuration because when giving one value without the other, the configuration is ignored
                @Test
                fun `should build config with datamaintain monitoring configuration`() {
                    // Given
                    val datamaintainMonitoringApiUrl = "https://url.com"
                    val datamaintainMonitoringModuleEnvironmentToken = "a78e96a7-6748-4f01-9691-ea3bf851ad43"

                    val updateDbArguments = listOf(
                        "--datamaintain-monitoring-api-url", datamaintainMonitoringApiUrl,
                        "--datamaintain-monitoring-module-environment-token", datamaintainMonitoringModuleEnvironmentToken
                    )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.monitoringConfiguration).isNotNull().and {
                        get { apiUrl }.isEqualTo(datamaintainMonitoringApiUrl)
                        get { moduleEnvironmentToken }.isEqualTo(datamaintainMonitoringModuleEnvironmentToken)
                    }
                }


                @Test
                fun `should ignore config when only given module environment token`() {
                    // Given
                    val datamaintainMonitoringModuleEnvironmentToken = "a78e96a7-6748-4f01-9691-ea3bf851ad43"

                    val updateDbArguments = listOf(
                        "--datamaintain-monitoring-module-environment-token", datamaintainMonitoringModuleEnvironmentToken
                    )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.monitoringConfiguration).isNull()
                }


                @Test
                fun `should ignore config when only given api url`() {
                    // Given
                    val datamaintainMonitoringApiUrl = "https://url.com"

                    val updateDbArguments = listOf(
                        "--datamaintain-monitoring-api-url", datamaintainMonitoringApiUrl,
                    )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.monitoringConfiguration).isNull()
                }
            }

            @Nested
            inner class ExecutionMode {
                @ParameterizedTest
                @EnumSource(
                    datamaintain.core.step.executor.ExecutionMode::class
                )
                @DisplayName("Should build config with execution mode {0}")
                fun `should build config with execution mode`(executionMode: datamaintain.core.step.executor.ExecutionMode) {
                    // Given
                    val updateDbArguments = listOf(
                            "--execution-mode", executionMode.name
                        )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.executor.executionMode).isEqualTo(executionMode)
                }
            }

            @Nested
            inner class Verbose {
                @Test
                fun `should build config with verbose set to true`() {
                    // Given
                    val updateDbArguments = listOf("--verbose")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.logs.verbose).isTrue()
                }

                @Test
                fun `should build config with verbose set to false`() {
                    // Given

                    // When
                    runUpdateDb()

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.logs.verbose).isFalse()
                }
            }

            @Nested
            inner class CreateTagsFromFolder {
                @Test
                fun `should build config with create tags from folder set to true`() {
                    // Given
                    val updateDbArguments = listOf("--create-tags-from-folder")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.scanner.doesCreateTagsFromFolder).isTrue()
                }

                @Test
                fun `should build config with create tags from folder set to false`() {
                    // Given

                    // When
                    runUpdateDb()

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.scanner.doesCreateTagsFromFolder).isFalse()
                }
            }

            @Nested
            inner class TagMatchers {
                @Test
                fun `should build config with absolute path matchers`() {
                    // Given
                    val tagMatcher1 = TagMatcher(Tag("MYTAG1"), listOf("/pathMatcher1", "/pathMatcher2"))
                    val tagMatcher2 = TagMatcher(Tag("MYTAG2"), listOf("/pathMatcher3", "/pathMatcher4"))

                    val updateDbArguments = 
                        listOf(
                            "--tag", tagMatcher1.toArgument(), "--tag", tagMatcher2.toArgument()
                        )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.scanner.tagsMatchers)
                        .containsExactlyInAnyOrder(tagMatcher1, tagMatcher2)
                }

                private fun TagMatcher.toArgument(): String = "${this.tag.name}=[${this.globPaths.joinToString(", ")}]"
            }

            @Nested
            inner class TagsList {
                @Test
                fun `should build config with blacklisted tags`() {
                    testBuildConfigWithTagsList("--blacklisted-tags", "blacklisted")
                }

                @Test
                fun `should build config with whitelisted tags`() {
                    testBuildConfigWithTagsList("--whitelisted-tags", "whitelisted")
                }

                @Test
                fun `should build config with tags to play again`() {
                    testBuildConfigWithTagsList("--tags-to-play-again", "play-again")
                }

                private fun testBuildConfigWithTagsList(key: String, tagsToTest: String) {
                    // Given
                    val tagsList = setOf("MYTAG", "MYOTHERTAG")

                    val updateDbArguments = 
                        listOf(
                            key, tagsList.joinToString(",")
                        )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    val tags = when (tagsToTest) {
                        "blacklisted" -> configWrapper.datamaintainConfig!!.filter.blacklistedTags
                        "whitelisted" -> configWrapper.datamaintainConfig!!.filter.whitelistedTags
                        "play-again" -> configWrapper.datamaintainConfig!!.pruner.tagsToPlayAgain
                        else -> error("unknown tagsToTest $tagsToTest")
                    }

                    expectThat(tags)
                        .map { it.name }
                        .containsExactlyInAnyOrder(tagsList)
                }
            }

            @Nested
            inner class Rules {
                @Test
                fun `should build config with one rule`() {
                    // Given
                    val updateDbArguments = 
                        listOf(
                            "--rule", SameScriptsAsExecutedCheck.NAME
                        )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper) {
                        get { datamaintainConfig }.isNotNull()
                    }
                    expectThat(configWrapper.datamaintainConfig!!.checker.rules) {
                        hasSize(1)
                        first().isEqualTo(SameScriptsAsExecutedCheck.NAME)
                    }
                }

                @Test
                fun `should build config with 2 rules`() {
                    // Given
                    val updateDbArguments = 
                        listOf(
                            "--rule", SameScriptsAsExecutedCheck.NAME,
                            "--rule", SameScriptsAsExecutedCheck.NAME
                        )

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper) {
                        get { datamaintainConfig }.isNotNull()
                    }
                    expectThat(configWrapper.datamaintainConfig!!.checker.rules) {
                        hasSize(2)
                        first().isEqualTo(SameScriptsAsExecutedCheck.NAME)
                        last().isEqualTo(SameScriptsAsExecutedCheck.NAME)
                    }
                }
            }

            @Nested
            inner class AllowAutoOverride {
                @Test
                fun `should build default config without auto override`() {
                    // Given

                    // When
                    runUpdateDb()

                    // Then
                    expectThat(configWrapper) {
                        get { datamaintainConfig }.isNotNull()
                    }
                    expectThat(configWrapper.datamaintainConfig!!.executor.overrideExecutedScripts) {
                        isFalse()
                    }
                }

                @Test
                fun `should build config with auto override`() {
                    // Given
                    val updateDbArguments = listOf("--allow-auto-override")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper) {
                        get { datamaintainConfig }.isNotNull()
                    }
                    expectThat(configWrapper.datamaintainConfig!!.executor.overrideExecutedScripts) {
                        isTrue()
                    }
                }
            }

            @Nested
            inner class Porcelain {
                @Test
                fun `should build default config without auto override`() {
                    // Given

                    // When
                    runUpdateDb()

                    // Then
                    expectThat(configWrapper) {
                        get { datamaintainConfig }.isNotNull()
                    }
                    expectThat(configWrapper.datamaintainConfig!!.logs.porcelain) {
                        isFalse()
                    }
                }

                @Test
                fun `should build config with auto override`() {
                    // Given
                    val updateDbArguments = listOf("--porcelain")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat(configWrapper) {
                        get { datamaintainConfig }.isNotNull()
                    }
                    expectThat(configWrapper.datamaintainConfig!!.logs.porcelain) {
                        isTrue()
                    }
                }
            }

            @ParameterizedTest
            @EnumSource(ScriptAction::class)
            @DisplayName("Should build config with default script action {0}")
            fun `should build config with script action`(scriptAction: ScriptAction) {
                // Given
                val updateDbArguments = 
                    listOf(
                        "--action", scriptAction.name
                    )

                // When
                runUpdateDb(updateDbArguments)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.executor.defaultScriptAction).isEqualTo(scriptAction)
            }
        }

        @Nested
        inner class MongoDriverConfiguration {
            @Nested
            inner class MongoSaveOutput {
                @Test
                fun `should build config with mongo save output set to true`() {
                    // Given
                    val updateDbArguments = listOf("--save-db-output")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { saveOutput }
                        .isTrue()
                }

                @Test
                fun `should build config with mongo save output set to false`() {
                    // Given

                    // When
                    runUpdateDb()

                    // Then
                    expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { saveOutput }
                        .isFalse()
                }
            }

            @Nested
            inner class MongoPrintOutput {
                @Test
                fun `should build config with create tags from folder set to true`() {
                    // Given
                    val updateDbArguments = listOf("--print-db-output")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { printOutput }
                        .isTrue()
                }

                @Test
                fun `should build config with create tags from folder set to false`() {
                    // Given

                    // When
                    runUpdateDb()

                    // Then
                    expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { printOutput }
                        .isFalse()
                }
            }

            @Nested
            inner class MongoShellConfig {
                @Test
                fun `should build config with mongo`() {
                    // Given
                    val updateDbArguments = listOf("--mongo-shell").plus("mongo")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .and {
                            get { mongoShell }.isEqualTo(MongoShell.MONGO)
                            get { clientPath }.isEqualTo(Paths.get("mongo"))
                        }
                }

                @Test
                fun `should build config with mongosh`() {
                    // Given
                    val updateDbArguments = listOf("--mongo-shell").plus("mongosh")

                    // When
                    runUpdateDb(updateDbArguments)

                    // Then
                    expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .and {
                            get { mongoShell }.isEqualTo(MongoShell.MONGOSH)
                            get { clientPath }.isEqualTo(Paths.get("mongosh"))
                        }
                }
            }
        }

        @Nested
        inner class Flags {
            @Test
            fun `should build config with one flag`() {
                // Given
                val updateDbArguments = listOf("--flags=MY_TEST_FLAG")

                // When
                runUpdateDb(updateDbArguments)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.executor.flags) {
                    containsExactly("MY_TEST_FLAG")
                }
            }

            @Test
            fun `should build config with two flags`() {
                // Given
                val updateDbArguments = listOf("--flags=MY_TEST_FLAG1,MY_TEST_FLAG2")

                // When
                runUpdateDb(updateDbArguments)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.executor.flags) {
                    containsExactly("MY_TEST_FLAG1", "MY_TEST_FLAG2")
                }
            }
        }
    }

    private fun runUpdateDb(updateDbArguments: List<String> = listOf()) {
        runAppWithUpdateDb(listOf("--db-type", "mongo", "--db-uri", "mongo-uri"), updateDbArguments)
    }
}
