package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.Tag
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.check.rules.implementations.SameScriptsAsExecutedCheck
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths
import kotlin.reflect.KProperty1

internal class AppTest {
    data class ConfigWrapper(var datamaintainConfig: DatamaintainConfig? = null)

    private val configWrapper = ConfigWrapper()

    private fun runner(config: DatamaintainConfig) {
        configWrapper.datamaintainConfig = config
    }

    @Nested
    inner class UpdateDb {
        @Nested
        inner class ConfigurationBuild {
            @Nested
            inner class GenericConfiguration {
                @Test
                fun `should build config with path`() {
                    // Given
                    val path = "myPath"

                    val argv = updateMongoDbMinimumArguments().plus(listOf(
                            "--path", path
                    ))

                    // When
                    runUpdateDb(argv)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.path).isEqualTo(Paths.get(path))
                }

                @Test
                fun `should build config with identifier regex`() {
                    // Given
                    val identifierRegex = "myIdentifierRegex"

                    val argv = updateMongoDbMinimumArguments().plus(listOf(
                            "--identifier-regex", identifierRegex
                    ))

                    // When
                    runUpdateDb(argv)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.identifierRegex.pattern).isEqualTo(identifierRegex)
                }

                @ParameterizedTest
                @EnumSource
                @DisplayName("Should build config with execution mode {0}")
                fun `should build config with execution mode`(executionMode: ExecutionMode) {
                    // Given
                    val argv = updateMongoDbMinimumArguments().plus(listOf(
                            "--execution-mode", executionMode.name
                    ))

                    // When
                    runUpdateDb(argv)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.executionMode).isEqualTo(executionMode)
                }

                @Nested
                inner class Verbose {
                    @Test
                    fun `should build config with verbose set to true`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus("--verbose")

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.verbose).isTrue()
                    }

                    @Test
                    fun `should build config with verbose set to false`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.verbose).isFalse()
                    }
                }

                @Nested
                inner class CreateTagsFromFolder {
                    @Test
                    fun `should build config with create tags from folder set to true`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus("--create-tags-from-folder")

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.doesCreateTagsFromFolder).isTrue()
                    }

                    @Test
                    fun `should build config with create tags from folder set to false`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.doesCreateTagsFromFolder).isFalse()
                    }
                }

                @Nested
                inner class TagMatchers {
                    @Test
                    fun `should build config with path matchers`() {
                        // Given
                        val tagMatcher1 = TagMatcher(Tag("MYTAG1"), listOf("pathMatcher1", "pathMatcher2"))
                        val tagMatcher2 = TagMatcher(Tag("MYTAG2"), listOf("pathMatcher3", "pathMatcher4"))

                        val argv = updateMongoDbMinimumArguments().plus(listOf(
                                "--tag", tagMatcher1.toArgument(), "--tag", tagMatcher2.toArgument()
                        ))

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.tagsMatchers)
                                .containsExactlyInAnyOrder(tagMatcher1, tagMatcher2)
                    }

                    private fun TagMatcher.toArgument(): String = "${this.tag.name}=[${this.globPaths.joinToString(", ")}]"
                }

                @Nested
                inner class TagsList {
                    @Test
                    fun `should build config with blacklisted tags`() {
                        testBuildConfigWithTagsList("--blacklisted-tags", DatamaintainConfig::blacklistedTags)
                    }

                    @Test
                    fun `should build config with whitelisted tags`() {
                        testBuildConfigWithTagsList("--whitelisted-tags", DatamaintainConfig::whitelistedTags)
                    }

                    @Test
                    fun `should build config with tags to play again`() {
                        testBuildConfigWithTagsList("--tags-to-play-again", DatamaintainConfig::tagsToPlayAgain)
                    }

                    private fun testBuildConfigWithTagsList(key: String, getter: KProperty1<DatamaintainConfig, Set<Tag>>) {
                        // Given
                        val tagsList = setOf("MYTAG", "MYOTHERTAG")

                        val argv = updateMongoDbMinimumArguments().plus(listOf(
                                key, tagsList.joinToString(",")
                        ))

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat(getter.get(configWrapper.datamaintainConfig!!))
                                .map { it.name }
                                .containsExactlyInAnyOrder(tagsList)
                    }
                }

                @Nested
                inner class Rules {
                    @Test
                    fun `should build config with one rule`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus(listOf(
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
                        val argv = updateMongoDbMinimumArguments().plus(listOf(
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
            }

            @Nested
            inner class MongoDriverConfiguration {
                @Nested
                inner class MongoSaveOutput {
                    @Test
                    fun `should build config with create tags from folder set to true`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus("--mongo-save-output")

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                                .get { saveOutput }
                                .isTrue()
                    }

                    @Test
                    fun `should build config with create tags from folder set to false`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runUpdateDb(argv)

                        // Then
                        expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                                .get { saveOutput }
                                .isFalse()
                    }
                }
            }
        }

        private fun runUpdateDb(argv: List<String>) {
            App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)
        }

        private fun updateMongoDbMinimumArguments(): List<String> {
            return listOf(
                    "--mongo-uri", "mongo-uri",
                    "update-db"
            )
        }
    }

    @Nested
    inner class Configuration
}