package datamaintain

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.FileScript
import datamaintain.core.script.Tag
import datamaintain.core.step.Filter
import datamaintain.core.step.executor.ExecutionMode
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class FilterTest {
    private val dbDriver = mockk<DatamaintainDriver>()
    private val otherTag = Tag("other")

    @Test
    fun `should filter blacklisted scripts`() {
        // Given
        val blacklistedTag = Tag("blacklistedTag")
        val context = Context(
                DatamaintainConfig(
                        Paths.get(""),
                        Regex(""),
                        false,
                        emptySet(),
                        setOf(blacklistedTag),
                        emptySet(),
                        emptySet(),
                        emptySequence(),
                        ExecutionMode.NORMAL,
                        FakeDriverConfig()),
                dbDriver = dbDriver)

        val filter = Filter(context)

        val scripts = listOf(
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/01_file1"),
                        Regex(""),
                        setOf(blacklistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/02_file2"),
                        Regex(""),
                        setOf(otherTag, blacklistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/03_file3"),
                        Regex(""),
                        setOf(otherTag)
                ),
                FileScript(Paths.get("src/test/resources/scanner_test_files/10_file10"), Regex("")))

        // When
        val filteredScript = filter.filter(scripts)

        // Then
        expectThat(filteredScript) {
            hasSize(2)
            get(0).get { this.name }.isEqualTo("03_file3")
            get(1).get { this.name }.isEqualTo("10_file10")
        }
    }

    @Test
    fun `should filter whitelisted scripts`() {
        // Given
        val whitelistedTag = Tag("whitelistedTag")
        val context = Context(
                DatamaintainConfig(
                        Paths.get(""),
                        Regex(""),
                        false,
                        setOf(whitelistedTag),
                        setOf(),
                        emptySet(),
                        emptySet(),
                        emptySequence(),
                        ExecutionMode.NORMAL,
                        FakeDriverConfig()),
                dbDriver = dbDriver)

        val filter = Filter(context)

        val scripts = listOf(
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/01_file1"),
                        Regex(""),
                        setOf(whitelistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/02_file2"),
                        Regex(""),
                        setOf(otherTag, whitelistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/03_file3"),
                        Regex(""),
                        setOf(otherTag)
                ),
                FileScript(Paths.get("src/test/resources/scanner_test_files/10_file10"), Regex("")))

        // When
        val filteredScript = filter.filter(scripts)

        // Then
        expectThat(filteredScript) {
            hasSize(2)
            get(0).get { this.name }.isEqualTo("01_file1")
            get(1).get { this.name }.isEqualTo("02_file2")
        }
    }

    @Test
    fun `should filter whitelisted and blacklisted scripts`() {
        // Given
        val whitelistedTag = Tag("whitelistedTag")
        val blacklistedTag = Tag("blacklistedTag")
        val blackAndWhitelistedTag = Tag("both")

        val whitelistedTags = setOf(whitelistedTag, blackAndWhitelistedTag)
        val blacklistedTags = setOf(blacklistedTag, blackAndWhitelistedTag)

        val context = Context(
                DatamaintainConfig(
                        Paths.get(""),
                        Regex(""),
                        false,
                        whitelistedTags,
                        blacklistedTags,
                        emptySet(),
                        emptySet(),
                        emptySequence(),
                        ExecutionMode.NORMAL,
                        FakeDriverConfig()),
                dbDriver = dbDriver)

        val filter = Filter(context)

        val scripts = listOf(
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/01_file1"),
                        Regex(""),
                        setOf(whitelistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/02_file2"),
                        Regex(""),
                        setOf(blackAndWhitelistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/03_file3"),
                        Regex(""),
                        setOf(blacklistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/03_file3"),
                        Regex(""),
                        setOf(Tag("bla"))
                ),
                FileScript(Paths.get("src/test/resources/scanner_test_files/10_file10"), Regex("")))

        // When
        val filteredScript = filter.filter(scripts)

        // Then
        expectThat(filteredScript) {
            hasSize(1)
            get(0).get { this.name }.isEqualTo("01_file1")
        }
    }
}
