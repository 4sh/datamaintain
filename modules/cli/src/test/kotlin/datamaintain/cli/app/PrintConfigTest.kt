package datamaintain.cli.app

import ch.qos.logback.classic.Logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.matches

internal class PrintConfigTest : BaseCliTest() {
    private val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    private val testAppender = TestAppender()

    @BeforeEach
    fun setupLogger() {
        logger.addAppender(testAppender)
        testAppender.start()
    }

    @Test
    fun `should print config when command is mark-script-as-executed`() {
        // Given
        val path = "/myPath"

        val markScriptAsExecutedArguments =
                listOf(
                        "--path", path
                )

        // When
        runAppWithMarkOneScriptAsExecuted(
                listOf(
                        "--config", "--db-type", "mongo", "--db-uri", "mongo-uri"
                ), markScriptAsExecutedArguments
        )

        // Then
        var index = 0
        expectThat(testAppender.events) {
            get { get(index++).message }.isEqualTo("Configuration: ")
            get { get(index++).message }.matches("- working directory -> .*/datamaintain/modules/cli".toRegex())
            get { get(index++).message }.isEqualTo("- path -> /myPath")
            get { get(index++).message }.isEqualTo("- identifier regex -> (.*)")
            get { get(index++).message }.isEqualTo("- script action -> MARK_AS_EXECUTED")
            get { get(index++).message }.isEqualTo("- execution mode -> NORMAL")
            get { get(index++).message }.isEqualTo("- tags -> []")
            get { get(index++).message }.isEqualTo("- whitelisted tags -> []")
            get { get(index++).message }.isEqualTo("- blacklisted tags -> []")
            get { get(index++).message }.isEqualTo("- tags to play again -> []")
            get { get(index++).message }.isEqualTo("- Allow override executed script -> false")
            get { get(index++).message }.isEqualTo("- rules -> []")
            get { get(index++).message }.isEqualTo("- verbose -> false")
            get { get(index++).message }.isEqualTo("- porcelain -> false")
        }
    }

    @Test
    fun `should print inherited config when command is mark-script-as-exectued`() {
        // Given
        val path = "/myPath"

        val markScriptAsExecutedArguments =
                listOf(
                        "--path", path
                )

        // When
        runAppWithMarkOneScriptAsExecuted(
                listOf(
                        "--config", "--db-type", "mongo", "--db-uri", "mongo-uri", "--config-file-path", "src/test/resources/config-child.properties"
                ), markScriptAsExecutedArguments
        )

        // Then
        var index = 0
        expectThat(testAppender.events) {
            get { get(index++).message }.matches(("Load new config keys from parent config located" +
                    " at .*/datamaintain/modules/cli/src/test/resources/config-parent\\.properties").toRegex())
            get { get(index++).message }.isEqualTo("Configuration: ")
            get { get(index++).message }.matches("- working directory -> .*/datamaintain/modules/cli/src/test/resources".toRegex())
            get { get(index++).message }.isEqualTo("- name -> childConfig")
            get { get(index++).message }.isEqualTo("- path -> /myPath")
            get { get(index++).message }.isEqualTo("- identifier regex -> (.*)")
            get { get(index++).message }.isEqualTo("- script action -> MARK_AS_EXECUTED")
            get { get(index++).message }.isEqualTo("- execution mode -> NORMAL")
            get { get(index++).message }.isEqualTo("- tags -> [TagMatcher(tag=Tag(name=MYTAG2), globPaths=[/pathMatcher2])]")
            get { get(index++).message }.isEqualTo("- whitelisted tags -> []")
            get { get(index++).message }.isEqualTo("- blacklisted tags -> []")
            get { get(index++).message }.isEqualTo("- tags to play again -> []")
            get { get(index++).message }.isEqualTo("- Allow override executed script -> false")
            get { get(index++).message }.isEqualTo("- rules -> []")
            get { get(index++).message }.isEqualTo("- verbose -> false")
            get { get(index++).message }.isEqualTo("- porcelain -> false")
        }
    }

    @Test
    fun `should print config when command is update-db`() {
        // Given
        val path = "/myPath"

        val updateDbArguments =
                listOf(
                        "--path", path,
                        "--identifier-regex", "v.*_",
                        "--execution-mode", "NORMAL",
                        "--action", "RUN",
                        "--tag", "MYTAG1=/pathMatcher1",
                        "--tag", "MYTAG2=/pathMatcher2",
                        "--blacklisted-tags", "MYTAG1",
                        "--whitelisted-tags", "MYTAG2",
                        "--verbose",
                        "--porcelain"
                )

        // When
        runAppWithUpdateDb(
                listOf(
                        "--config", "--db-type", "mongo", "--db-uri", "mongo-uri"
                ), updateDbArguments
        )

        // Then
        var index = 0
        expectThat(testAppender.events) {
            get { get(index++).message }.isEqualTo("Configuration: ")
            get { get(index++).message }.matches("- working directory -> .*/datamaintain/modules/cli".toRegex())
            get { get(index++).message }.isEqualTo("- path -> /myPath")
            get { get(index++).message }.isEqualTo("- identifier regex -> v.*_")
            get { get(index++).message }.isEqualTo("- script action -> RUN")
            get { get(index++).message }.isEqualTo("- execution mode -> NORMAL")
            get { get(index++).message }.isEqualTo("- tags -> [TagMatcher(tag=Tag(name=MYTAG2), globPaths=[/pathMatcher2]), TagMatcher(tag=Tag(name=MYTAG1), globPaths=[/pathMatcher1])]")
            get { get(index++).message }.isEqualTo("- whitelisted tags -> [Tag(name=MYTAG2)]")
            get { get(index++).message }.isEqualTo("- blacklisted tags -> [Tag(name=MYTAG1)]")
            get { get(index++).message }.isEqualTo("- tags to play again -> []")
            get { get(index++).message }.isEqualTo("- Allow override executed script -> false")
            get { get(index++).message }.isEqualTo("- rules -> []")
            get { get(index++).message }.isEqualTo("- verbose -> true")
            get { get(index++).message }.isEqualTo("- porcelain -> true")
        }
    }

    @Test
    fun `should print inherited config when command is update-db`() {
        // Given
        val path = "/myPath"

        val updateDbArguments =
                listOf(
                        "--path", path,
                        "--identifier-regex", "v.*_",
                        "--execution-mode", "DRY",
                        "--action", "RUN",
                        "--tag", "MYTAG1=/pathMatcher1",
                        "--blacklisted-tags", "MYTAG1",
                        "--whitelisted-tags", "MYTAG2",
                        "--verbose",
                        "--porcelain"
                )

        // When
        runAppWithUpdateDb(
                listOf(
                        "--config", "--db-type", "mongo", "--db-uri", "mongo-uri", "--config-file-path", "src/test/resources/config-child.properties"
                ), updateDbArguments
        )

        // Then
        var index = 0
        expectThat(testAppender.events) {
            get { get(index++).message }.matches(("Load new config keys from parent config located" +
                    " at .*/datamaintain/modules/cli/src/test/resources/config-parent\\.properties").toRegex())
            get { get(index++).message }.isEqualTo("Configuration: ")
            get { get(index++).message }.matches("- working directory -> .*/datamaintain/modules/cli/src/test/resources".toRegex())
            get { get(index++).message }.isEqualTo("- name -> childConfig")
            get { get(index++).message }.isEqualTo("- path -> /myPath")
            get { get(index++).message }.isEqualTo("- identifier regex -> v.*_")
            get { get(index++).message }.isEqualTo("- script action -> RUN")
            get { get(index++).message }.isEqualTo("- execution mode -> DRY")
            get { get(index++).message }.isEqualTo("- tags -> [TagMatcher(tag=Tag(name=MYTAG2), globPaths=[/pathMatcher2]), TagMatcher(tag=Tag(name=MYTAG1), globPaths=[/pathMatcher1])]")
            get { get(index++).message }.isEqualTo("- whitelisted tags -> [Tag(name=MYTAG2)]")
            get { get(index++).message }.isEqualTo("- blacklisted tags -> [Tag(name=MYTAG1)]")
            get { get(index++).message }.isEqualTo("- tags to play again -> []")
            get { get(index++).message }.isEqualTo("- Allow override executed script -> false")
            get { get(index++).message }.isEqualTo("- rules -> []")
            get { get(index++).message }.isEqualTo("- verbose -> true")
            get { get(index++).message }.isEqualTo("- porcelain -> true")
        }
    }
}
