package datamaintain.core.step.check

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.step.check.rules.implementations.ExecutedScriptsNotRemovedCheck
import datamaintain.core.step.executor.Execution
import datamaintain.test.ScriptWithContentWithFixedChecksum
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.lang.IllegalArgumentException
import java.nio.file.Paths

internal class CheckerTest {
    private val dbDriverMock = mockk<DatamaintainDriver>()

    private val script1 = ScriptWithContentWithFixedChecksum("1", "1", "1")
    private val script2 = ScriptWithContentWithFixedChecksum("2", "2", "2")
    private val script3 = ScriptWithContentWithFixedChecksum("3", "3", "3")
    private val script4 = ScriptWithContentWithFixedChecksum("4", "4", "4")

    private val executedScript1 = ExecutedScript("1", "1", "1", ExecutionStatus.OK)
    private val executedScript2 = ExecutedScript("2", "2", "2", ExecutionStatus.OK)

    val checkerData = CheckerData(
            sequenceOf(script1, script2, script3, script4),
            sequenceOf(script1, script2, script3, script4),
            sequenceOf(script1, script2, script3, script4),
            sequenceOf(script3, script4)
    )

    @Test
    fun `should succeed when no check rule`() {
        // Given
        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf()),
                dbDriver = dbDriverMock
        )

        val checker = Checker(context)

        // When
        val scripts = checker.check(checkerData)

        // Then
        expectThat(scripts) {
            hasSize(2)
            get(0).get { this.name }.isEqualTo("3")
            get(1).get { this.name }.isEqualTo("4")
        }
    }

    @Test
    fun `should succeed when check rules succeed`() {
        // Given
        every { dbDriverMock.listExecutedScripts() }.answers { sequenceOf(executedScript1, executedScript2) }

        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf(ExecutedScriptsNotRemovedCheck.NAME)),
                dbDriver = dbDriverMock
        )

        val checker = Checker(context)

        // When
        val scripts = checker.check(checkerData)

        // Then
        expectThat(scripts) {
            hasSize(2)
            get(0).get { this.name }.isEqualTo("3")
            get(1).get { this.name }.isEqualTo("4")
        }
    }

    @Test
    fun `should failed when check rule name doesn't exist`() {
        // Given
        every { dbDriverMock.listExecutedScripts() }.answers { sequenceOf(executedScript1, executedScript2) }

        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf("sadfsdf")),
                dbDriver = dbDriverMock
        )

        val checker = Checker(context)

        // WhenThen
        expectThrows<IllegalArgumentException> { checker.check(checkerData) }
    }

    @Test
    fun `should failed without launching any rule when a check rule name doesn't exist`() {
        // Given
        every { dbDriverMock.listExecutedScripts() }.answers { sequenceOf(executedScript1, executedScript2) }

        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf(ExecutedScriptsNotRemovedCheck.NAME, "sadfsdf")),
                dbDriver = dbDriverMock
        )

        val checker = Checker(context)

        // WhenThen
        expectThrows<IllegalArgumentException> { checker.check(CheckerData()) }
    }
}