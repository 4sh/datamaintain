package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.Tag
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.check.rules.implementations.SameScriptsAsExecutedCheck
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectCatching
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
                    runApp(argv)

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
                    runApp(argv)

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
                    runApp(argv)

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
                        runApp(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.verbose).isTrue()
                    }

                    @Test
                    fun `should build config with verbose set to false`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runApp(argv)

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
                        runApp(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.doesCreateTagsFromFolder).isTrue()
                    }

                    @Test
                    fun `should build config with create tags from folder set to false`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runApp(argv)

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
                        runApp(argv)

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
                        runApp(argv)

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
                        runApp(argv)

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
                        runApp(argv)

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
                    fun `should build config with mongo save output set to true`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus("--mongo-save-output")

                        // When
                        runApp(argv)

                        // Then
                        expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                                .get { saveOutput }
                                .isTrue()
                    }

                    @Test
                    fun `should build config with mongo save output set to false`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runApp(argv)

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
                        val argv = updateMongoDbMinimumArguments().plus("--mongo-print-output")

                        // When
                        runApp(argv)

                        // Then
                        expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                                .get { printOutput }
                                .isTrue()
                    }

                    @Test
                    fun `should build config with create tags from folder set to false`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runApp(argv)

                        // Then
                        expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                                .get { printOutput }
                                .isFalse()
                    }
                }
            }
        }

        private fun updateMongoDbMinimumArguments(): List<String> {
            return listOf(
                    "--mongo-uri", "mongo-uri",
                    "update-db"
            )
        }
    }

    @Nested
    inner class BaseConfiguration {
        @Nested
        inner class ConfigFilePath {
            @Test
            fun `should build configuration with existing config file path`() {
                // Given
                val argv = listOf("--config-file-path", "src/test/resources/config.properties", "update-db")

                // When
                runApp(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { mongoUri }
                        .isEqualTo("mongo-uri")
            }

            @Test
            @Disabled
            //TODO@ERO: fix this when PR #120 is merged
            fun `should throw error when specified config file path does not exist`() {
                // Given
                val argv = listOf("--config-file-path", "non-existing-file.properties", "update-db")

                // When
                runApp(argv)

                // Then

            }
        }

        @Nested
        inner class DbType {
            @Test
            fun `should build configuration with mongo db type`() {
                // Given
                val argv = listOf("--db-type", datamaintain.cli.DbType.MONGO.value, "--mongo-uri", "mongoUri", "update-db")

                // When
                runApp(argv)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.driverConfig).isA<MongoDriverConfig>()
            }

            @Test
            @Disabled("Does not work")
            //TODO@ERO: fix this when PR #120 is merged
            fun `should throw error when given db type is not valid`() {
                // Given
                val argv = listOf("--db-type", "invalid db type", "update-db")

                // When

                // Then
                expectCatching { runApp(argv) }
                        .failed()
                        .isA<DbTypeNotFoundException>()
            }
        }

        @Test
        fun `should build configuration with mongo uri`() {
            // Given
            val mongoUri = "my great mongo uri"
            val argv = listOf("--mongo-uri", mongoUri, "update-db")

            // When
            runApp(argv)

            // Then
            expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                    .get { mongoUri }.isEqualTo(mongoUri)
        }
    }

    private fun runApp(argv: List<String>) {
        App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)
    }
}