package datamaintain.sort

import datamaintain.Config
import datamaintain.Script
import datamaintain.ScriptWithoutContent
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class ByLengthAndCaseInsensitiveAlphabeticalSorterTest {
    private val caseInsensitiveAlphabeticalSorter: ByLengthAndCaseInsensitiveAlphabeticalSorter = ByLengthAndCaseInsensitiveAlphabeticalSorter(Config(
            Paths.get(""), "", "", Regex("")))

    @Test
    fun `should sort scripts list by name`() {
        // Given
        val superScript = ScriptWithoutContent("super script", "checksum", "")
        val greatScript = ScriptWithoutContent("great script", "checksum", "")

        // When
        expectThat(caseInsensitiveAlphabeticalSorter.sort(listOf(
                superScript,
                greatScript),
                Script::name)) {
            // Then
            first().isEqualTo(greatScript)
            last().isEqualTo(superScript)
        }
    }

    @Test
    fun `should sort scripts list by name containing numbers`() {
        // Given
        val script2 = ScriptWithoutContent("2", "checksum", "")
        val script1 = ScriptWithoutContent("1", "checksum", "")
        val script10 = ScriptWithoutContent("10", "checksum", "")

        // When
        expectThat(caseInsensitiveAlphabeticalSorter.sort(listOf(
                script2,
                script1,
                script10),
                Script::name)) {
            // Then
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
            last().isEqualTo(script10)
        }
    }

    @Test
    fun `should sort scripts list by name containing numbers, letters and caps`() {
        // Given
        val script2 = ScriptWithoutContent("Script2", "checksum", "")
        val script1 = ScriptWithoutContent("scrIpt1", "checksum", "")
        val script10 = ScriptWithoutContent("script10", "checksum", "")

        // When
        expectThat(caseInsensitiveAlphabeticalSorter.sort(listOf(
                script2,
                script1,
                script10),
                Script::name)) {
            // Then
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
            last().isEqualTo(script10)
        }
    }

    @Test
    fun `should sort scripts list by identifier`() {
        // Given
        val superScript = ScriptWithoutContent("super script", "checksum1", "2")
        val greatScript = ScriptWithoutContent("great script", "checksum2", "1")
        val script = ScriptWithoutContent("script", "checksum3", "11")

        // When
        expectThat(caseInsensitiveAlphabeticalSorter.sort(listOf(superScript, greatScript, script), Script::identifier)) {
            // Then
            size.isEqualTo(3)
            first().isEqualTo(greatScript)
            get(1).isEqualTo(superScript)
            last().isEqualTo(script)
        }
    }
}
