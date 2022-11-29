package datamaintain.cli.app.utils

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class PorcelainUtilTest {
    @Test
    fun should_extract_relative_path_without_start_slash() {
        // Given
        val scanPath = Paths.get("/scan/path")
        val filePath = Paths.get("/scan/path/files/my_file")

        // When
        val actualRelativePath = extractRelativePath(scanPath = scanPath, filePath = filePath)

        // Then
        expectThat(actualRelativePath).isEqualTo("files/my_file")
    }

    @Test
    fun should_extract_relative_path() {
        // Given
        val scanPath = Paths.get("/scan/path/")
        val filePath = Paths.get("/scan/path/files/my_file")

        // When
        val actualRelativePath = extractRelativePath(scanPath = scanPath, filePath = filePath)

        // Then
        expectThat(actualRelativePath).isEqualTo("files/my_file")
    }
}
