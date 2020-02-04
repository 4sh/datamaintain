package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.report.Report
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ExecutionStatus.*
import datamaintain.core.script.InMemoryScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.core.step.executor.Executor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.hasSize
import strikt.assertions.map
import java.nio.file.Paths

internal class ExecutorTest {
    private val dbDriverMock = mockk<DatamaintainDriver>()
    private val context = Context(DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig()), dbDriver = dbDriverMock)

    private val executor = Executor(context)

    private val script1 = InMemoryScript("1", "1", "1")
    private val script2 = InMemoryScript("2", "2", "2")
    private val script3 = InMemoryScript("3", "3", "3")

    @Test
    fun `should execute and build invalid report`() {
        // Given
        every { dbDriverMock.executeScript(eq(script2)) }.answers {
            generateKoExecutedScript(it.invocation.args.first() as ScriptWithContent)
        }
        every { dbDriverMock.executeScript(neq(script2)) }.answers {
            generateOkExecutedScript(it.invocation.args.first() as ScriptWithContent)
        }
        every { dbDriverMock.markAsExecuted(any()) }.returnsArgument(0)

        // When
        val report = executor.execute(listOf(script1, script2, script3))

        // Then
        verify(exactly = 2) { dbDriverMock.executeScript(any()) }
        verify(exactly = 1) { dbDriverMock.markAsExecuted(any()) }

        expectThat(report) {
            get { executedScripts }
                    .hasSize(2)
                    .and {
                        map { it.name }
                                .containsExactly(script1.name, script2.name)
                    }
                    .and {
                        map { it.executionStatus }
                                .containsExactly(OK, KO)
                    }
        }
    }

    @Test
    fun `should execute and build valid report`() {
        // Given
        every { dbDriverMock.executeScript(any()) }.answers {
            generateOkExecutedScript(it.invocation.args.first() as ScriptWithContent)
        }
        every { dbDriverMock.markAsExecuted(any()) }.answers { it.invocation.args.first() as ExecutedScript }

        // When
        val report = executor.execute(listOf(script1, script2, script3))

        // Then
        verify(exactly = 3) { dbDriverMock.executeScript(any()) }
        verify(exactly = 3) { dbDriverMock.markAsExecuted(any()) }

        expectThat(report) {
            get { executedScripts }
                    .hasSize(3)
                    .and {
                        map { it.name }
                                .containsExactly(script1.name, script2.name, script3.name)
                    }
                    .and {
                        map { it.executionStatus }
                                .containsExactly(OK, OK, OK)
                    }
        }
    }

    @Test
    fun `should execute forcing mark as executed`() {
        // Given
        every { dbDriverMock.markAsExecuted(any()) }.returnsArgument(0)

        val context = Context(DatamaintainConfig(
                Paths.get(""),
                Regex(""),
                driverConfig = FakeDriverConfig(),
                executionMode = ExecutionMode.FORCE_MARK_AS_EXECUTED
        ), dbDriver = dbDriverMock)
        val executor = Executor(context)

        // When
        val report: Report = executor.execute(listOf(script1, script2, script3))

        // Then
        verify(exactly = 0) { dbDriverMock.executeScript(any()) }
        verify(exactly = 3) { dbDriverMock.markAsExecuted(any()) }

        expectThat(report) {
            get { executedScripts }
                    .hasSize(3)
                    .and {
                        map { it.name }
                                .containsExactly(script1.name, script2.name, script3.name)
                    }
                    .and {
                        map { it.executionStatus }
                                .containsExactly(FORCE_MARKED_AS_EXECUTED, FORCE_MARKED_AS_EXECUTED, FORCE_MARKED_AS_EXECUTED)
                    }
        }
    }

    @Test
    fun `should execute without script and build valid report`() {
        // Given

        // When
        val report = executor.execute(listOf())

        // Then
        verify(exactly = 0) { dbDriverMock.executeScript(any()) }
        verify(exactly = 0) { dbDriverMock.markAsExecuted(any()) }

        expectThat(report) {
            get { executedScripts }
                    .hasSize(0)
        }
    }

    @Test
    fun `should not call executeScript when execution mode is dry`() {
        // Given
        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""),
                        driverConfig = FakeDriverConfig(),
                        executionMode = ExecutionMode.DRY),
                dbDriver = dbDriverMock)
        val executor = Executor(context)

        // When
        val report = executor.execute(listOf(script1, script2, script3))

        // Then
        verify(exactly = 0) { dbDriverMock.executeScript(any()) }
        verify(exactly = 0) { dbDriverMock.markAsExecuted(any()) }

        expectThat(report) {
            get { executedScripts }
                    .hasSize(3)
                    .and {
                        map { it.name }
                                .containsExactly(script1.name, script2.name, script3.name)
                    }
                    .and {
                        map { it.executionStatus }
                                .containsExactly(SHOULD_BE_EXECUTED, SHOULD_BE_EXECUTED, SHOULD_BE_EXECUTED)
                    }
        }
    }

    private fun generateOkExecutedScript(script: ScriptWithContent) =
            ExecutedScript(script.name, "", script.identifier, OK)

    private fun generateKoExecutedScript(script: ScriptWithContent) =
            ExecutedScript(script.name, "", script.identifier, KO)
}
