package datamaintain.core.script

import datamaintain.core.exception.DatamaintainFileIdentifierPatternException
import datamaintain.test.buildDatamaintainConfig
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.io.File
import java.nio.file.Paths

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

    @Test
    fun `should not compute the porcelainName when porcelain is set to false`() {
        //Given
        val config = buildDatamaintainConfig(path = Paths.get("/scan/path"), porcelain = false)
        val scriptFile = File("/scan/path/files/my_file")

        //WHEN
        val fileScript = FileScript.from(config = config, tags = setOf(), scriptFile = scriptFile)

        //THEN
        expectThat(fileScript.porcelainName)
            .isNull()
    }

    @Test
    fun `should compute the porcelainName when porcelain is set to true`() {
        //Given
        val config = buildDatamaintainConfig(path = Paths.get("/scan/path"), porcelain = true)
        val scriptFile = File("/scan/path/files/my_file")

        //WHEN
        val fileScript = FileScript.from(config = config, tags = setOf(), scriptFile = scriptFile)

        //THEN
        expectThat(fileScript.porcelainName)
            .isNotNull()
            .isEqualTo("files/my_file")
    }
}
