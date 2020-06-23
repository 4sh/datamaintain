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

internal class ByCaseInsensitiveSeparatorFreeAlphabeticalSorterTest  {
    private val sorter: ByCaseInsensitiveSeparatorFreeAlphabeticalSorter = ByCaseInsensitiveSeparatorFreeAlphabeticalSorter(DatamaintainConfig(
            Paths.get(""), Regex(""), driverConfig = FakeDriverConfig()))

    @Test
    fun `should sort scripts list by identifier with only digit`() {
        // Given
        val script1 = ExecutedScript("v1", "checksum1", "1", ExecutionStatus.OK)
        val script2 = ExecutedScript("v1.1", "checksum2", "1.1", ExecutionStatus.OK)
        val script3 = ExecutedScript("v1.1.0-1", "checksum3", "1.1.0-1", ExecutionStatus.OK)
        val script4 = ExecutedScript("v1.2", "checksum4", "1.2", ExecutionStatus.OK)
        val script5 = ExecutedScript("v11", "checksum5", "11", ExecutionStatus.OK)

        // When
        expectThat(sorter.sort(listOf(script4, script1, script5, script3, script2), Script::identifier)) {
            // Then
            size.isEqualTo(5)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
            get(2).isEqualTo(script3)
            get(3).isEqualTo(script4)
            last().isEqualTo(script5)
        }
    }

    @Test
    fun `should sort scripts list by identifier with only char`() {
        // Given
        val script1 = ExecutedScript("va", "checksum1", "a", ExecutionStatus.OK)
        val script2 = ExecutedScript("va.a", "checksum2", "a.a", ExecutionStatus.OK)
        val script3 = ExecutedScript("va.a.b-a", "checksum3", "a.a.b-a", ExecutionStatus.OK)
        val script4 = ExecutedScript("va.b", "checksum4", "a.b", ExecutionStatus.OK)
        val script5 = ExecutedScript("vaa", "checksum5", "aa", ExecutionStatus.OK)

        // When
        expectThat(sorter.sort(listOf(script4, script1, script5, script3, script2), Script::identifier)) {
            // Then
            size.isEqualTo(5)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
            get(2).isEqualTo(script3)
            get(3).isEqualTo(script4)
            last().isEqualTo(script5)
        }
    }

    @Test
    fun `should sort scripts list by identifier`() {
        // Given
        val script1 = ExecutedScript("va", "checksum1", "a", ExecutionStatus.OK)
        val script2 = ExecutedScript("va.1", "checksum2", "a.1", ExecutionStatus.OK)
        val script3 = ExecutedScript("va.1.b-1", "checksum3", "a.1.b-1", ExecutionStatus.OK)
        val script4 = ExecutedScript("va.1.b-2", "checksum4", "a.1.b-2", ExecutionStatus.OK)
        val script5 = ExecutedScript("va.1.c", "checksum5", "a.1.c", ExecutionStatus.OK)
        val script6 = ExecutedScript("va.2", "checksum6", "a.2", ExecutionStatus.OK)
        val script7 = ExecutedScript("vaa", "checksum7", "aa", ExecutionStatus.OK)

        // When
        expectThat(sorter.sort(listOf(script4, script6, script1, script5, script3, script7, script2), Script::identifier)) {
            // Then
            size.isEqualTo(7)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
            get(2).isEqualTo(script3)
            get(3).isEqualTo(script4)
            get(4).isEqualTo(script5)
            get(5).isEqualTo(script6)
            last().isEqualTo(script7)
        }
    }

    @Test
    fun `should sort scripts list by name containing numbers, letters and caps`() {
        // Given
        val script2 = ExecutedScript("Script2", "checksum", "Script2", ExecutionStatus.OK)
        val script1 = ExecutedScript("scrIpt1", "checksum", "scrIpt1", ExecutionStatus.OK)
        val script10 = ExecutedScript("script10", "checksum", "script10", ExecutionStatus.OK)

        // When
        expectThat(sorter.sort(listOf(
                script2,
                script1,
                script10),
                Script::name)) {
            // Then
            first().isEqualTo(script1)
            get(1).isEqualTo(script10)
            last().isEqualTo(script2)
        }
    }
}
