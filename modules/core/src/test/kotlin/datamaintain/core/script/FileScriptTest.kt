package datamaintain.core.script

import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.failed
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.nio.file.Paths
import datamaintain.core.exception.DatamaintainFileIdentifierPatternException

internal class FileScriptTest {
    @Test
    fun `should extract id`() {
        // Given
        val path = Paths.get("v1.1_removeEmoSecurityLevel.js")
        val identifierRegex = Regex("(.*)_.*")

        // When
        val fileScript = FileScript(path, identifierRegex)

        // Then
        expectThat(fileScript).and {
            get { identifier }.isEqualTo("v1.1")
        }
    }

    @Test
    fun `should throw error when extracting id`() {
        // Given
        val path = Paths.get("v1.1-removeEmoSecurityLevel.js")
        val identifierRegex = Regex("(.*)_.*")

        // When
        val fileScript = FileScript(path, identifierRegex)

        // Then
        expectCatching { fileScript.identifier }
                .failed()
                .isA<DatamaintainFileIdentifierPatternException>()
    }
}
