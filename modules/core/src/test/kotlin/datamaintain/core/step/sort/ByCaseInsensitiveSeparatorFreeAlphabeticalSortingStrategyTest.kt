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
        val superScript = ExecutedScript("super script", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)
        val greatScript = ExecutedScript("great script", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script2 = ExecutedScript("2", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script1 = ExecutedScript("1", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script10 = ExecutedScript("10", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script2 = ExecutedScript("Script2", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script1 = ExecutedScript("scrIpt1", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script10 = ExecutedScript("script10", "checksum", "", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val superScript = ExecutedScript("super script", "checksum1", "2", ExecutionStatus.OK,
                ScriptAction.RUN)
        val greatScript = ExecutedScript("great script", "checksum2", "1", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script = ExecutedScript("script", "checksum3", "11", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script1 = ExecutedScript("v1", "checksum1", "1", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script2 = ExecutedScript("v1.1", "checksum2", "1.1", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script3 = ExecutedScript("v1.1.0-1", "checksum3", "1.1.0-1", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script4 = ExecutedScript("v1.2", "checksum4", "1.2", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script5 = ExecutedScript("v11", "checksum5", "11", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script6 = ExecutedScript("v101", "checksum6", "101", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script1 = ExecutedScript("va", "checksum1", "a", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script2 = ExecutedScript("va.a", "checksum2", "a.a", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script3 = ExecutedScript("va.a.b-a", "checksum3", "a.a.b-a", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script4 = ExecutedScript("va.b", "checksum4", "a.b", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script5 = ExecutedScript("vaa", "checksum5", "aa", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script1 = ExecutedScript("va", "checksum1", "a", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script2 = ExecutedScript("va.1", "checksum2", "a.1", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script3 = ExecutedScript("va.1.b-1", "checksum3", "a.1.b-1", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script4 = ExecutedScript("va.1.b-2", "checksum4", "a.1.b-2", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script5 = ExecutedScript("va.1.c", "checksum5", "a.1.c", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script6 = ExecutedScript("va.2", "checksum6", "a.2", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script7 = ExecutedScript("vaa", "checksum7", "aa", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script1 = ExecutedScript("1", "checksum1", "abc", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script2 = ExecutedScript("2", "checksum2", "bc", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script1 = ExecutedScript("1", "checksum1", "v0.1", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script2 = ExecutedScript("2", "checksum2", "v1", ExecutionStatus.OK,
                ScriptAction.RUN)

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
        val script1 = ExecutedScript("1", "checksum1", "1.0001", ExecutionStatus.OK,
                ScriptAction.RUN)
        val script2 = ExecutedScript("2", "checksum2", "1.0002", ExecutionStatus.OK,
                ScriptAction.RUN)

        // When
        expectThat(sortingStrategy.sort(listOf(script1, script2), Script::identifier)) {
            // Then
            size.isEqualTo(2)
            first().isEqualTo(script1)
            get(1).isEqualTo(script2)
        }
    }
}
