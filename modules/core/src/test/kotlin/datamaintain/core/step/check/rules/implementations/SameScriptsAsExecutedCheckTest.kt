package datamaintain.core.step.check.rules.implementations

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ScriptAction
import datamaintain.test.ScriptWithContentWithFixedChecksum
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.succeeded

internal class SameScriptsAsExecutedCheckTest {
    private val script1 = ScriptWithContentWithFixedChecksum("script1", "1", "1")
    private val script2 = ScriptWithContentWithFixedChecksum("script2", "2", "2")
    private val script3 = ScriptWithContentWithFixedChecksum("script3", "3", "3")
    private val script4 = ScriptWithContentWithFixedChecksum("script4", "4", "4")

    private val executedScript1 = ExecutedScript("script1", "1", "1", ExecutionStatus.OK,
            ScriptAction.RUN)
    private val executedScript2 = ExecutedScript("script2", "2", "2", ExecutionStatus.OK,
            ScriptAction.RUN)
    private val executedScript3 = ExecutedScript("script3", "3", "3", ExecutionStatus.OK,
            ScriptAction.RUN)

    @Test
    fun `should succeed with empty sequences`() {
        // Given
        val checker = SameScriptsAsExecutedCheck(emptySequence())

        // WhenThen
        expectCatching { checker.check(emptySequence()) }
                .succeeded()
    }

    @Test
    fun `should succeed with same element in both list`() {
        // Given
        val checker = SameScriptsAsExecutedCheck(sequenceOf(executedScript1))

        // WhenThen
        expectCatching { checker.check(sequenceOf(script1)) }
                .succeeded()
    }

    @Test
    fun `should succeed when scripts is an overset of executedScript`() {
        // Given
        val checker = SameScriptsAsExecutedCheck(sequenceOf(executedScript1, executedScript2))

        // WhenThen
        expectCatching { checker.check(sequenceOf(script1, script2, script3)) }
                .succeeded()
    }

    @Test
    fun `should failed when scripts is a subset of executedScript`() {
        // Given
        val checker = SameScriptsAsExecutedCheck(sequenceOf(executedScript1, executedScript2, executedScript3))

        // WhenThen
        expectThrows<IllegalStateException> { checker.check(sequenceOf(script1, script2)) }
                .get { message }
                .isEqualTo("ERROR - ${SameScriptsAsExecutedCheck.NAME} - " +
                        "Some executed scripts are not present : [${executedScript3.name}]")
    }

    @Test
    fun `should failed when scripts is a subset (with new elements) of executedScript`() {
        // Given
        val checker = SameScriptsAsExecutedCheck(sequenceOf(executedScript1, executedScript2, executedScript3))

        // WhenThen
        expectThrows<IllegalStateException> { checker.check(sequenceOf(script1, script4)) }
                .get { message }
                .isEqualTo("ERROR - ${SameScriptsAsExecutedCheck.NAME} - " +
                        "Some executed scripts are not present : [${executedScript2.name}, ${executedScript3.name}]")
    }
}