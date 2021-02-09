package datamaintain.core.step.check

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.exception.DatamaintainCheckRuleNotFoundException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.step.check.rules.implementations.AlwaysFailedCheck
import datamaintain.core.step.check.rules.implementations.AlwaysSucceedCheck
import datamaintain.test.ScriptWithContentWithFixedChecksum
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class CheckerTest {
    private val dbDriverMock = mockk<DatamaintainDriver>()

    private val script1 = ScriptWithContentWithFixedChecksum("1", "1", "1")
    private val script2 = ScriptWithContentWithFixedChecksum("2", "2", "2")
    private val script3 = ScriptWithContentWithFixedChecksum("3", "3", "3")
    private val script4 = ScriptWithContentWithFixedChecksum("4", "4", "4")

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
        every { dbDriverMock.listExecutedScripts() }.answers { sequenceOf() }

        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf(AlwaysSucceedCheck.NAME)),
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
    fun `should failed when check rule failed`() {
        // Given
        every { dbDriverMock.listExecutedScripts() }.answers { sequenceOf() }

        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf(AlwaysFailedCheck.NAME)),
                dbDriver = dbDriverMock
        )

        val checker = Checker(context)

        // WhenThen
        expectThrows<DatamaintainException> { checker.check(checkerData) }
                .get { message }
                .isEqualTo("ERROR - ${AlwaysFailedCheck.NAME} - Use this rule for tests only")
    }

    @Test
    fun `should failed when check rule name doesn't exist`() {
        // Given
        every { dbDriverMock.listExecutedScripts() }.answers { sequenceOf() }

        val badRuleName = "sadfsdf"
        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf(badRuleName)),
                dbDriver = dbDriverMock
        )

        val checker = Checker(context)

        // WhenThen
        expectThrows<DatamaintainException> { checker.check(checkerData) }
                .get { message }
                .isEqualTo("Aborting - Check rule `${badRuleName}` not found")
    }

    @Test
    fun `should failed without launching any rule when a check rule name doesn't exist`() {
        // Given
        every { dbDriverMock.listExecutedScripts() }.answers { sequenceOf() }

        val badRuleName = "sadfsdf"
        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig(),
                        checkRules = sequenceOf(AlwaysFailedCheck.NAME, badRuleName)),
                dbDriver = dbDriverMock
        )

        val checker = Checker(context)

        // WhenThen
        expectThrows<DatamaintainException> { checker.check(CheckerData()) }
                .get { message }
                .isEqualTo("Aborting - Check rule `${badRuleName}` not found")
    }
}
