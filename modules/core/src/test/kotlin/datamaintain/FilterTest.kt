package datamaintain

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.Context
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.FileScript
import datamaintain.core.script.Tag
import datamaintain.core.step.Filter
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class FilterTest {
    private val dbDriver = mockk<DatamaintainDriver>()
    private val blacklistedTag = Tag("blacklistedTag")
    private val context = Context(
            DatamaintainConfig(
                    Paths.get(""),
                    Regex(""),
                    setOf(blacklistedTag),
                    FakeDriverConfig()),
            dbDriver = dbDriver)

    private val filter = Filter(context)

    @Test
    fun `should filter blacklisted scripts`() {
        val normalTag = Tag("other")

        // Given
        val scripts = listOf(
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/01_file1"),
                        Regex(""),
                        setOf(blacklistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/02_file2"),
                        Regex(""),
                        setOf(normalTag, blacklistedTag)
                ),
                FileScript(
                        Paths.get("src/test/resources/scanner_test_files/03_file3"),
                        Regex(""),
                        setOf(normalTag)
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
}