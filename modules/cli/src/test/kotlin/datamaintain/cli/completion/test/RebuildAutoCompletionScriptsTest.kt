package datamaintain.cli.completion.test

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import datamaintain.cli.completion.*
import kotlin.io.path.createTempDirectory

internal class RebuildAutoCompletionScriptsTest {
    @Test
    fun `should generate bash script`() {
        // Given
        val path = createTempDirectory()

        // When
        generateAutoCompletionScripts(path.toString())

        // Then
        // TODO: verify files exist in output path and have the right contents
        expectThat(1).isEqualTo(2)
    }

}