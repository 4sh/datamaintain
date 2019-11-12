package datamaintain.sort

import datamaintain.Config
import datamaintain.ScriptWithoutContent
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.first
import strikt.assertions.get
import strikt.assertions.isEqualTo
import strikt.assertions.last
import java.nio.file.Paths

internal class AlphabeticalSorterTest {
    private val alphabeticalSorter: AlphabeticalSorter = AlphabeticalSorter(Config(
            Paths.get(""), Regex(""), "", ""))

    @Test
    fun `should sort scripts list by name`() {
        // Given
        val superScript = ScriptWithoutContent("super script", "checksum", "")
        val greatScript = ScriptWithoutContent("great script", "checksum", "")

        // When
        expectThat(alphabeticalSorter.sort(listOf(
                superScript,
                greatScript))) {
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
        expectThat(alphabeticalSorter.sort(listOf(
                script2,
                script1,
                script10))) {
            // Then
            first().isEqualTo(script1)
            get(1).isEqualTo(script10)
            last().isEqualTo(script2)
        }
    }

    @Test
    fun `should sort scripts list by name containing numbers, letters and caps`() {
        // Given
        val script2 = ScriptWithoutContent("Script2", "checksum", "")
        val script1 = ScriptWithoutContent("scrIpt1", "checksum", "")
        val script10 = ScriptWithoutContent("script10", "checksum", "")

        // When
        expectThat(alphabeticalSorter.sort(listOf(
                script2,
                script1,
                script10))) {
            // Then
            first().isEqualTo(script1)
            get(1).isEqualTo(script10)
            last().isEqualTo(script2)
        }
    }
}