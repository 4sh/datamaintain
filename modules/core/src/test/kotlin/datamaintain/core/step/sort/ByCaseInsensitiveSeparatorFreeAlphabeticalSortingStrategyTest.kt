package datamaintain.core.step.sort

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.Script
import datamaintain.core.script.ScriptAction
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*

internal class ByCaseInsensitiveSeparatorFreeAlphabeticalSortingStrategyTest  {
    private val sortingStrategy = ByCaseInsensitiveSeparatorFreeAlphabeticalSortingStrategy()

    @Test
    fun `should sort scripts list by name`() {
        // Given
        val superScript = buildExecutedScript("super script", "checksum", "")
        val greatScript = buildExecutedScript("great script", "checksum", "")

        // When
        expectThat(sortingStrategy.sort(listOf(
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
        val script2 = buildExecutedScript("2", "checksum", "")
        val script1 = buildExecutedScript("1", "checksum", "")
        val script10 = buildExecutedScript("10", "checksum", "")

        // When
        expectThat(sortingStrategy.sort(listOf(
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
        val script2 = buildExecutedScript("Script2", "checksum", "")
        val script1 = buildExecutedScript("scrIpt1", "checksum", "")
        val script10 = buildExecutedScript("script10", "checksum", "")

        // When
        expectThat(sortingStrategy.sort(listOf(
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

    @Test
    fun `should sort scripts list by identifier`() {
        // Given
        val superScript = buildExecutedScript("super script", "checksum1", "2")
        val greatScript = buildExecutedScript("great script", "checksum2", "1")
        val script = buildExecutedScript("script", "checksum3", "11")

        // When
        expectThat(sortingStrategy.sort(listOf(superScript, greatScript, script), Script::identifier)) {
            // Then
            size.isEqualTo(3)
            first().isEqualTo(greatScript)
            get(1).isEqualTo(superScript)
            last().isEqualTo(script)
        }
    }

    @Test
    fun `should sort scripts list by complex identifier with only digit`() {
        // Given
        val script1 = buildExecutedScript("v1", "checksum1", "1")
        val script2 = buildExecutedScript("v1.1", "checksum2", "1.1")
        val script3 = buildExecutedScript("v1.1.0-1", "checksum3", "1.1.0-1")
        val script4 = buildExecutedScript("v1.2", "checksum4", "1.2")
        val script5 = buildExecutedScript("v11", "checksum5", "11")
        val script6 = buildExecutedScript("v101", "checksum6", "101")

        // When
        expectThat(sortingStrategy.sort(listOf(script4, script1, script5, script3, script6, script2), Script::identifier)) {
            // Then
            size.isEqualTo(6)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
            get(2).isEqualTo(script3)
            get(3).isEqualTo(script4)
            get(4).isEqualTo(script5)
            last().isEqualTo(script6)
        }
    }

    @Test
    fun `should sort scripts list by complex identifier with only char`() {
        // Given
        val script1 = buildExecutedScript("va", "checksum1", "a")
        val script2 = buildExecutedScript("va.a", "checksum2", "a.a")
        val script3 = buildExecutedScript("va.a.b-a", "checksum3", "a.a.b-a")
        val script4 = buildExecutedScript("va.b", "checksum4", "a.b")
        val script5 = buildExecutedScript("vaa", "checksum5", "aa")

        // When
        expectThat(sortingStrategy.sort(listOf(script4, script1, script5, script3, script2), Script::identifier)) {
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
    fun `should sort scripts list by complex identifier`() {
        // Given
        val script1 = buildExecutedScript("va", "checksum1", "a")
        val script2 = buildExecutedScript("va.1", "checksum2", "a.1")
        val script3 = buildExecutedScript("va.1.b-1", "checksum3", "a.1.b-1")
        val script4 = buildExecutedScript("va.1.b-2", "checksum4", "a.1.b-2")
        val script5 = buildExecutedScript("va.1.c", "checksum5", "a.1.c")
        val script6 = buildExecutedScript("va.2", "checksum6", "a.2")
        val script7 = buildExecutedScript("vaa", "checksum7", "aa")

        // When
        expectThat(sortingStrategy.sort(listOf(script4, script6, script1, script5, script3, script7, script2), Script::identifier)) {
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
    fun `should sort scripts list by complex identifier1`() {
        // Given
        val script1 = buildExecutedScript("1", "checksum1", "abc")
        val script2 = buildExecutedScript("2", "checksum2", "bc")

        // When
        expectThat(sortingStrategy.sort(listOf(script1, script2), Script::identifier)) {
            // Then
            size.isEqualTo(2)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
        }
    }

    @Test
    fun `should sort scripts list by complex identifier2`() {
        // Given
        val script1 = buildExecutedScript("1", "checksum1", "v0.1")
        val script2 = buildExecutedScript("2", "checksum2", "v1")

        // When
        expectThat(sortingStrategy.sort(listOf(script1, script2), Script::identifier)) {
            // Then
            size.isEqualTo(2)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
        }
    }

    @Test
    fun `should sort scripts list by complex identifier3`() {
        // Given
        val script1 = buildExecutedScript("1", "checksum1", "1.0001")
        val script2 = buildExecutedScript("2", "checksum2", "1.0002")

        // When
        expectThat(sortingStrategy.sort(listOf(script1, script2), Script::identifier)) {
            // Then
            size.isEqualTo(2)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
        }
    }

    private fun buildExecutedScript(name: String, checksum: String, identifier: String) = ExecutedScript(
            name, checksum, identifier, ExecutionStatus.OK, ScriptAction.RUN)
}
