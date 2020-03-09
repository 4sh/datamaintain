package datamaintain.core.step.sort

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.Script
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class ByLengthAndCaseInsensitiveAlphabeticalSorterTest {
    private val caseInsensitiveAlphabeticalSorter: ByLengthAndCaseInsensitiveAlphabeticalSorter = ByLengthAndCaseInsensitiveAlphabeticalSorter(DatamaintainConfig(
            Paths.get(""), Regex(""), driverConfig = FakeDriverConfig()))

    @Test
    fun `should sort scripts list by name`() {
        // Given
        val superScript = ExecutedScript("super script", "checksum", "", ExecutionStatus.OK)
        val greatScript = ExecutedScript("great script", "checksum", "", ExecutionStatus.OK)

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
        val script2 = ExecutedScript("2", "checksum", "", ExecutionStatus.OK)
        val script1 = ExecutedScript("1", "checksum", "", ExecutionStatus.OK)
        val script10 = ExecutedScript("10", "checksum", "", ExecutionStatus.OK)

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
        val script2 = ExecutedScript("Script2", "checksum", "", ExecutionStatus.OK)
        val script1 = ExecutedScript("scrIpt1", "checksum", "", ExecutionStatus.OK)
        val script10 = ExecutedScript("script10", "checksum", "", ExecutionStatus.OK)

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
        val superScript = ExecutedScript("super script", "checksum1", "2", ExecutionStatus.OK)
        val greatScript = ExecutedScript("great script", "checksum2", "1", ExecutionStatus.OK)
        val script = ExecutedScript("script", "checksum3", "11", ExecutionStatus.OK)

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
