package datamaintain

import datamaintain.report.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.nio.file.Paths
import java.time.Instant

internal class ExecutorTest {
    private val dbDriverMock = mockk<DatamaintainDriver>()
    private val executor = Executor(Config(Paths.get(""), "", dbDriverMock))

    private val script1 = FileScript(Paths.get("1"))
    private val script2 = FileScript(Paths.get("2"))
    private val script3 = FileScript(Paths.get("3"))

    private val errorMessage = "Ko error"
    private val errorScript = TestScriptLineReport(Instant.now(), errorMessage, ExecutionStatus.KO)

    private val okMessage = "OK"
    private val okScript = TestScriptLineReport(Instant.now(), okMessage, ExecutionStatus.OK)

    @Test
    fun `should execute and build invalid report`() {
        // Given
        every { dbDriverMock.executeScript(eq(script1)) }.returns(errorScript)
        every { dbDriverMock.executeScript(neq(script1)) }.returns(okScript)

        // When
        val executionReport: ExecutionReport = executor.execute(listOf(script1, script2, script3))

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.KO)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.KO)
                    get { message }.isEqualTo(errorMessage)
                    get { script }.isEqualTo(script1)
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(okMessage)
                    get { script }.isEqualTo(script2)
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(okMessage)
                    get { script }.isEqualTo(script3)
                }
            }
        }
    }

    @Test
    fun `should execute and build valid report`() {
        // Given
        every { dbDriverMock.executeScript(any()) }.returns(okScript)

        // When
        val executionReport: ExecutionReport = executor.execute(listOf(script1, script2, script3))

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(okMessage)
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(okMessage)
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { message }.isEqualTo(okMessage)
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
        verify (exactly = 0){ dbDriverMock.executeScript(any()) }
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(0)
            }
        }
    }
}