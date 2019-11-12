package datamaintain

import datamaintain.db.drivers.DatamaintainDriver
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class PrunerTest {
    private val dbDriver = mockk<DatamaintainDriver>()
    val config = Config(Paths.get(""), Regex(""), "", "") withDriver dbDriver

    private val pruner = Pruner(config)

    @Test
    fun `should prune executed scripts`() {
        // Given
        val scripts = listOf(FileScript(Paths.get("src/test/resources/scanner_test_files/01_file1"), Regex("")),
                FileScript(Paths.get("src/test/resources/scanner_test_files/02_file2"), Regex("")),
                FileScript(Paths.get("src/test/resources/scanner_test_files/10_file10"), Regex(""))
        )

        every { dbDriver.listExecutedScripts() }
                .returns(sequenceOf(ScriptWithoutContent("01_file1", "c4ca4238a0b923820dcc509a6f75849b", "01")))

        // When
        val prunedScripts = pruner.prune(scripts)

        // Then
        expectThat(prunedScripts) {
            hasSize(2)
            get(0).get { this.name }.isEqualTo("02_file2")
            get(1).get { this.name }.isEqualTo("10_file10")
        }
    }

    @Test
    fun `should prune executed scripts when script has been renamed`() {
        // Given
        val scripts = listOf(FileScript(Paths.get("src/test/resources/scanner_test_files/01_file1"), Regex("")),
                FileScript(Paths.get("src/test/resources/scanner_test_files/02_file2"), Regex("")),
                FileScript(Paths.get("src/test/resources/scanner_test_files/10_file10"), Regex(""))
        )

        every { dbDriver.listExecutedScripts() }
                .returns(sequenceOf(ScriptWithoutContent("01_file1_renamed", "c4ca4238a0b923820dcc509a6f75849b", "01")))

        // When
        val prunedScripts = pruner.prune(scripts)

        // Then
        expectThat(prunedScripts) {
            hasSize(2)
            get(0).get { this.name }.isEqualTo("02_file2")
            get(1).get { this.name }.isEqualTo("10_file10")
        }
    }
}