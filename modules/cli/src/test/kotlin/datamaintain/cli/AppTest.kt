package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.ScriptAction
import datamaintain.core.script.Tag
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.check.rules.implementations.SameScriptsAsExecutedCheck
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.db.driver.mongo.MongoDriverConfig
import datamaintain.db.driver.mongo.MongoShell
import datamaintain.test.execAppInSubprocess
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
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

    private fun buildPathToConfigFile(fileName: String): String = "src/test/resources/${fileName}.properties"

    private val configFilePath = buildPathToConfigFile("config")

    private val configWithoutDbTypePath = buildPathToConfigFile("config-without-db-type")

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
                    expectThat(configWrapper.datamaintainConfig!!.path).isEqualTo(Paths.get(path).toAbsolutePath())
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

                @Nested
                inner class ExecutionMode {
                    @ParameterizedTest
                    @EnumSource(datamaintain.core.step.executor.ExecutionMode::class,
                            names = ["FORCE_MARK_AS_EXECUTED"],
                            mode = EnumSource.Mode.EXCLUDE)
                    @DisplayName("Should build config with execution mode {0}")
                    fun `should build config with execution mode`(executionMode: datamaintain.core.step.executor.ExecutionMode) {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus(listOf(
                                "--execution-mode", executionMode.name
                        ))

                        // When
                        runApp(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.executionMode).isEqualTo(executionMode)
                    }

                    @Test
                    fun `should build config with FORCE_MARK_AS_EXECUTED as execution mode`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus(listOf(
                                "--execution-mode", datamaintain.core.step.executor.ExecutionMode.FORCE_MARK_AS_EXECUTED.name
                        ))

                        // When
                        runApp(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!).and{
                            get { executionMode }.isEqualTo(datamaintain.core.step.executor.ExecutionMode.NORMAL)
                            get { defaultScriptAction }.isEqualTo(ScriptAction.MARK_AS_EXECUTED)
                        }
                    }
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

                        val argv = updateMongoDbMinimumArguments().plus(
                            listOf(
                                "--tag", tagMatcher1.toArgument(), "--tag", tagMatcher2.toArgument()
                            )
                        )

                        val scanPathString = CoreConfigKey.SCAN_PATH.default!!
                        val scanPath = Paths.get(scanPathString).toAbsolutePath().normalize()

                        val tagMatcher1FullPath = TagMatcher(
                            Tag("MYTAG1"),
                            tagMatcher1.globPaths.map { scanPath.resolve(it).toString() })

                        val tagMatcher2FullPath = TagMatcher(
                            Tag("MYTAG2"),
                            tagMatcher2.globPaths.map { scanPath.resolve(it).toString() })

                        // When
                        runApp(argv)

                        // Then
                        expectThat(configWrapper.datamaintainConfig!!.tagsMatchers)
                            .containsExactlyInAnyOrder(tagMatcher1FullPath, tagMatcher2FullPath)
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

                @Nested
                inner class AllowAutoOverride {
                    @Test
                    fun `should build default config without auto override`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runApp(argv)

                        // Then
                        expectThat(configWrapper) {
                            get { datamaintainConfig }.isNotNull()
                        }
                        expectThat(configWrapper.datamaintainConfig!!.overrideExecutedScripts) {
                            isFalse()
                        }
                    }

                    @Test
                    fun `should build config with auto override`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus("--allow-auto-override")

                        // When
                        runApp(argv)

                        // Then
                        expectThat(configWrapper) {
                            get { datamaintainConfig }.isNotNull()
                        }
                        expectThat(configWrapper.datamaintainConfig!!.overrideExecutedScripts) {
                            isTrue()
                        }
                    }
                }

                @ParameterizedTest
                @EnumSource(ScriptAction::class)
                @DisplayName("Should build config with default script action {0}")
                fun `should build config with script action`(scriptAction: ScriptAction) {
                    // Given
                    val argv = updateMongoDbMinimumArguments().plus(listOf(
                            "--action", scriptAction.name
                    ))

                    // When
                    runApp(argv)

                    // Then
                    expectThat(configWrapper.datamaintainConfig!!.defaultScriptAction).isEqualTo(scriptAction)
                }

                @Nested
                inner class Porcelain {
                    @Test
                    fun `should build default config without auto override`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments()

                        // When
                        runApp(argv)

                        // Then
                        expectThat(configWrapper) {
                            get { datamaintainConfig }.isNotNull()
                        }
                        expectThat(configWrapper.datamaintainConfig!!.porcelain) {
                            isFalse()
                        }
                    }

                    @Test
                    fun `should build config with auto override`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus("--porcelain")

                        // When
                        runApp(argv)

                        // Then
                        expectThat(configWrapper) {
                            get { datamaintainConfig }.isNotNull()
                        }
                        expectThat(configWrapper.datamaintainConfig!!.porcelain) {
                            isTrue()
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

                @Nested
                inner class MongoSh {
                    @Test
                    fun `should build config with mongosh`() {
                        // Given
                        val argv = updateMongoDbMinimumArguments().plus("--mongo-shell").plus("mongosh")

                        // When
                        runApp(argv)

                        // Then
                        expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                            .and {
                                get { mongoShell }.isEqualTo(MongoShell.MONGOSH)
                                get { clientPath }.isEqualTo(Paths.get("mongosh"))
                            }
                    }
                }
            }
        }

        private fun updateMongoDbMinimumArguments(): List<String> {
            return listOf(
                    "--db-type", "mongo",
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
                val argv = listOf("--config-file-path", configFilePath, "update-db")

                // When
                runApp(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { mongoUri }
                        .isEqualTo("mongo-uri")
            }

            @Test
            fun `should throw error when specified config file path does not exist`() {
                // Given
                val argv = listOf("--config-file-path", "non-existing-file.properties", "update-db")

                // When
                val (exitCode, output) = execAppInSubprocess(argv)

                // Then
                expectThat(exitCode).isEqualTo(1)
                expectThat(output).contains("java.io.FileNotFoundException: non-existing-file.properties (No such file or directory)")
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
            fun `should build configuration with mongo db type from file config`() {
                // Given
                val argv = listOf("--config-file-path", configFilePath, "update-db")

                // When
                runApp(argv)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.driverConfig).isA<MongoDriverConfig>()
            }

            @Test
            fun `should read config from key "dbType" when provided`() {
                // Given
                val argv = listOf("--config-file-path", configWithoutDbTypePath, "--db-type", "mongo", "update-db")

                // When
                runApp(argv)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.driverConfig).isA<MongoDriverConfig>()
            }

            @Test
            fun `should set mongo as db type when no db type was provided`() {
                // Given
                val argv = listOf("--config-file-path", configWithoutDbTypePath, "update-db")

                // When
                runApp(argv)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.driverConfig).isA<MongoDriverConfig>()
            }

            @Test
            fun `should throw error when given db type is not valid`() {
                // Given
                val argv = listOf("--db-type", "invalid db type", "update-db")

                // When
                val (exitCode, output) = execAppInSubprocess(argv)

                // Then
                expectThat(exitCode).isEqualTo(1)
                expectThat(output).contains("dbType invalid db type is unknown")
            }

            @Test
            @Disabled(
                "To ensure backward compatibility, mongo is provided as db type when no type is provided. " +
                        "Please reactivate this test when it is no longer the case"
            )
            fun `should throw error when no db type was provided`() {
                // Given
                val argv = listOf("--config-file-path", configWithoutDbTypePath, "update-db")

                // When
                val (exitCode, output) = execAppInSubprocess(argv)

                // Then
                expectThat(exitCode).isEqualTo(1)
                expectThat(output).contains("dbType must not be null")
            }
        }

        @Test
        fun `should build configuration with mongo uri`() {
            // Given
            val mongoUri = "my great mongo uri"
            val argv = listOf("--db-type", "mongo","--mongo-uri", mongoUri, "update-db")

            // When
            runApp(argv)

            // Then
            expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                    .get { mongoUri }.isEqualTo(mongoUri)
        }

        @Test
        fun `should build configuration with mongo tmp path`() {
            // Given
            val mongoTmpPath = "my mongo tmp path"
            val argv = listOf("--db-type", "mongo", "--mongo-uri", "mongouri", "--mongo-tmp-path", mongoTmpPath, "update-db")

            // When
            runApp(argv)

            // Then
            expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                    .get { mongoTmpPath }.isEqualTo(mongoTmpPath)
        }

        @Nested
        inner class TrustUri {
            @Test
            fun `should build config with trust uri`() {
                // Given
                val argv = listOf(
                        "--trust-uri",
                        "--db-type",
                        datamaintain.cli.DbType.MONGO.value,
                        "--mongo-uri",
                        "mongoUri",
                        "update-db"
                )

                // When
                runApp(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig as MongoDriverConfig).trustUri) {
                    isTrue()
                }
            }

            @Test
            fun `should build config without trust uri`() {
                // Given
                val argv = listOf(
                        "--db-type",
                        datamaintain.cli.DbType.MONGO.value,
                        "--mongo-uri",
                        "mongoUri",
                        "update-db"
                )

                // When
                runApp(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig as MongoDriverConfig).trustUri) {
                    isFalse()
                }
            }
        }
    }

    private fun runApp(argv: List<String>) {
        App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)
    }
}
