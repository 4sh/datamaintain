package datamaintain.cli.completion.test

import datamaintain.cli.app.datamaintainApp
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import datamaintain.cli.completion.*

import java.io.File
import kotlin.io.path.createTempDirectory

internal class RebuildAutoCompletionScriptsTest {
    @Test
    fun `should generate bash script`() {
        // Given
        val path = createTempDirectory()

        // When
        generateAutoCompletionScripts(path.toString())

        // Then
        val lineList = mutableListOf<String>()
        val bashFileName = path.toString() + "/bash-autocomplete.sh"
        File(bashFileName).useLines { lines -> lines.forEach { lineList.add(it) }}
        expectThat(lineList[0]).isEqualTo("#!/usr/bin/env bash")
    }

    @Test
    fun `should generate zsh script`() {
        // Given
        val path = createTempDirectory()

        // When
        generateAutoCompletionScripts(path.toString())

        // Then
        val lineList = mutableListOf<String>()
        val bashFileName = path.toString() + "/zsh-autocomplete.sh"
        File(bashFileName).useLines { lines -> lines.forEach { lineList.add(it) }}
        expectThat(lineList[0]).isEqualTo("#!/usr/bin/env zsh")
    }

}