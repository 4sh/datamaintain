package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.Tag
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.check.rules.implementations.SameScriptsAsExecutedCheck
import org.junit.jupiter.api.Nested
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Test
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
            expectThat(configWrapper.datamaintainConfig!!.path).isEqualTo(Paths.get(path))
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
            expectThat(configWrapper.datamaintainConfig!!.identifierRegex.pattern).isEqualTo(identifierRegex)
        }

        @Nested
        inner class TagMatchers {
            @Test
            fun `should build config with path matchers`() {
                // Given
                val tagMatcher1 = TagMatcher(Tag("MYTAG1"), listOf("pathMatcher1", "pathMatcher2"))
                val tagMatcher2 = TagMatcher(Tag("MYTAG2"), listOf("pathMatcher3", "pathMatcher4"))

                val argv = updateDbMinimumArguments().plus(listOf(
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

                val argv = updateDbMinimumArguments().plus(listOf(
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