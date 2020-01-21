package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.report.ExecutionLineReport.Companion.correctExecutionMessage
import datamaintain.core.report.ExecutionLineReport.Companion.errorExecutionMessage
import datamaintain.core.report.ExecutionLineReport.Companion.forceMarkMessage
import datamaintain.core.report.ExecutionReport
import datamaintain.core.report.ReportStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.Script
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.core.step.executor.Executor
import io.mockk.MockKAnswerScope
import datamaintain.core.script.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class ExecutorTest {
    private val dbDriverMock = mockk<DatamaintainDriver>()
    private val context = Context(DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig()), dbDriver = dbDriverMock)

    private val executor = Executor(context)

    private val script1 = InMemoryScript("1", "1", "1")
    private val script2 = InMemoryScript("2", "2", "2")
    private val script3 = InMemoryScript("3", "3", "3")

    private val errorMessage = "Ko error"
    private val okMessage = "OK"

    @Test
    fun `should execute and build invalid report`() {
        // Given
        every { dbDriverMock.executeScript(eq(script1)) }.answers {
            generateKoExecutedScript(it.invocation.args.first() as ScriptWithContent)
        }
        every { dbDriverMock.executeScript(neq(script1)) }.answers {
            generateOkExecutedScript(it.invocation.args.first() as ScriptWithContent)
        }
        every { dbDriverMock.markAsExecuted(any()) }.answers { it.invocation.args.first() as ExecutedScript }

        // When
        val executionReport: ExecutionReport = executor.execute(listOf(script1, script2, script3))

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.KO)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.KO)
                    get { message }.isEqualTo(errorExecutionMessage(script1.name))
                    get { script }.isEqualTo(generateKoExecutedScript(script1))
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(correctExecutionMessage(script2.name))
                    get { script }.isEqualTo(generateOkExecutedScript(script2))
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(correctExecutionMessage(script3.name))
                    get { script }.isEqualTo(generateOkExecutedScript(script3))
                }
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
        val executionReport: ExecutionReport = executor.execute(listOf(script1, script2, script3))

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(correctExecutionMessage(script1.name))
                    get { script }.isEqualTo(generateOkExecutedScript(script1))
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(correctExecutionMessage(script2.name))
                    get { script }.isEqualTo(generateOkExecutedScript(script2))
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(correctExecutionMessage(script3.name))
                    get { script }.isEqualTo(generateOkExecutedScript(script3))
                }
            }
        }
    }

    @Test
    fun `should execute forcing mark as executed`() {
        // Given
        every { dbDriverMock.markAsExecuted(any()) }.answers { it.invocation.args.first() as ExecutedScript }

        val context = Context(
                DatamaintainConfig(Paths.get(""), Regex(""), driverConfig = FakeDriverConfig()),
                dbDriver = dbDriverMock,
                onlyMarkAsExecuted = true
        )
        val executor = Executor(context)

        // When
        val executionReport: ExecutionReport = executor.execute(listOf(script1, script2, script3))

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(forceMarkMessage(script1.name))
                    get { script }.isEqualTo(ExecutedScript(
                            script1.name,
                            script1.checksum,
                            script1.identifier,
                            ExecutionStatus.OK,
                            markAsExecutedForced = true
                    ))
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(forceMarkMessage(script2.name))
                    get { script }.isEqualTo(ExecutedScript(
                            script2.name,
                            script2.checksum,
                            script2.identifier,
                            ExecutionStatus.OK,
                            markAsExecutedForced = true
                    ))
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(forceMarkMessage(script3.name))
                    get { script }.isEqualTo(ExecutedScript(
                            script3.name,
                            script3.checksum,
                            script3.identifier,
                            ExecutionStatus.OK,
                            markAsExecutedForced = true
                    ))
                }
            }
        }
    }

    @Test
    fun `should execute without script and build valid report`() {
        // Given

        // When
        val executionReport: ExecutionReport = executor.execute(listOf())

        // Then
        verify(exactly = 0) { dbDriverMock.executeScript(any()) }
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(0)
            }
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
        val executionReport: ExecutionReport = executor.execute(listOf(script1, script2, script3))

        // Then
        verify(exactly = 0) { dbDriverMock.executeScript(any()) }
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEmpty()
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEmpty()
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEmpty()
                }
            }
        }
    }

    private fun generateOkExecutedScript(script: ScriptWithContent) =
            ExecutedScript(script.name, "", script.identifier, ExecutionStatus.OK)

    private fun generateKoExecutedScript(script: ScriptWithContent) =
            ExecutedScript(script.name, "", script.identifier, ExecutionStatus.KO)
}
