package datamaintain.sort

import datamaintain.Config
import datamaintain.ScriptWithoutContent
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class IdentifierSorterTest {
    private val identifierSorter: IdentifierSorter = IdentifierSorter(Config(
            Paths.get(""), "", Regex("")
    ))

    @Test
    fun `should sort scripts list by identifier`() {
        // Given
        val superScript = ScriptWithoutContent("super script", "checksum1", "2")
        val greatScript = ScriptWithoutContent("great script", "checksum2", "1")
        val script = ScriptWithoutContent("script", "checksum3", "11")

        // When
        expectThat(identifierSorter.sort(listOf(superScript, greatScript, script))) {
            // Then
            size.isEqualTo(3)
            first().isEqualTo(greatScript)
            get(1).isEqualTo(script)
            last().isEqualTo(superScript)
        }
    }
}