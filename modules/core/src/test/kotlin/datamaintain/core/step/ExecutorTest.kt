package datamaintain.core.step

import ch.qos.logback.classic.Logger
import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.script.InMemoryScript
import datamaintain.core.step.executor.Execution
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.core.step.executor.Executor
import datamaintain.core.step.executor.buildExecutedScript
import datamaintain.core.util.exception.DatamaintainQueryException
import datamaintain.test.TestAppender
import datamaintain.domain.report.Report
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ExecutionStatus.KO
import datamaintain.domain.script.ExecutionStatus.OK
import datamaintain.domain.script.ScriptAction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*
import java.nio.file.Paths

internal class ExecutorTest {
    private val dbDriverMock = mockk<DatamaintainDriver>()
    private val context = Context(
            DatamaintainConfig(
                    path = Paths.get(""),
                    identifierRegex = Regex(""),
                    driverConfig = FakeDriverConfig()),
            dbDriver = dbDriverMock
    )

    private val executor = Executor(context)

    private val script1 = InMemoryScript("1", "1", "1")
    private val script2 = InMemoryScript("2", "2", "2")
    private val script3 = InMemoryScript("3", "3", "3")

    @Test
    fun `should execute and build invalid report`() {
        // Given
        every { dbDriverMock.executeScript(eq(script2)) }.answers { Execution(KO) }
        every { dbDriverMock.executeScript(neq(script2)) }.answers { Execution(OK) }
        every { dbDriverMock.markAsExecuted(any()) }.returnsArgument(0)

        // WhenThen
        expectThrows<DatamaintainException> { executor.execute(listOf(script1, script2, script3)) }
            .and {
                get { report }
                    .get { executedScripts }
                    .hasSize(2)
                    .and {
                        map { it.name }
                            .containsExactly(script1.name, script2.name)
                    }
                    .and {
                        map { it.executionStatus }
                            .containsExactly(OK, KO)
                    }
                get { message }.isEqualTo("${script2.name} has not been correctly executed")
            }

        verify(exactly = 2) { dbDriverMock.executeScript(any()) }
        verify(exactly = 1) { dbDriverMock.markAsExecuted(any()) }
    }

    @Test
    fun `should execute and build valid report`() {
        // Given
        every { dbDriverMock.executeScript(any()) }.answers { Execution(OK) }
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
                path = Paths.get(""),
                identifierRegex = Regex(""),
                driverConfig = FakeDriverConfig(),
                executionMode = ExecutionMode.NORMAL
        ), dbDriver = dbDriverMock)
        val executor = Executor(context)

        val scripts = listOf(
                script1.copy(action = ScriptAction.MARK_AS_EXECUTED),
                script2.copy(action = ScriptAction.MARK_AS_EXECUTED),
                script3.copy(action = ScriptAction.MARK_AS_EXECUTED)
        )

        // When
        val report: Report = executor.execute(scripts)

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
                                .containsExactly(OK, OK, OK)
                    }
                    .and {
                        map { it.executionDurationInMillis }
                                .containsExactly(null, null, null)
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
                DatamaintainConfig(
                        path = Paths.get(""),
                        identifierRegex = Regex(""),
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
                                .containsExactly(OK, OK, OK)
                    }
                    .and {
                        map { it.executionDurationInMillis }
                                .containsExactly(null, null, null)
                    }
        }
    }

    @Test
    fun `should execute and produce executed scripts with flags`() {
        // Given
        val context = Context(
            DatamaintainConfig(
                path = Paths.get(""),
                identifierRegex = Regex(""),
                driverConfig = FakeDriverConfig(),
                flags = listOf("FLAG1", "FLAG2", "FLAG3")
            ),
            dbDriver = dbDriverMock
        )

        val executor = Executor(context)

        every { dbDriverMock.executeScript(any()) }.answers { Execution(OK) }
        every { dbDriverMock.markAsExecuted(any()) }.answers { it.invocation.args.first() as ExecutedScript }

        // When
        val report = executor.execute(listOf(script1, script2, script3))

        // Then
        expectThat(report) {
            get { executedScripts }.and {
                hasSize(3)
                get(0).and {
                    get { name }.isEqualTo(script1.name)
                    get { executionStatus }.isEqualTo(OK)
                    get { flags }.and {
                        containsExactly("FLAG1", "FLAG2", "FLAG3")
                    }
                }
                get(1).and {
                    get { name }.isEqualTo(script2.name)
                    get { executionStatus }.isEqualTo(OK)
                    get { flags }.and {
                        containsExactly("FLAG1", "FLAG2", "FLAG3")
                    }
                }
                get(2).and {
                    get { name }.isEqualTo(script3.name)
                    get { executionStatus }.isEqualTo(OK)
                    get { flags }.and {
                        containsExactly("FLAG1", "FLAG2", "FLAG3")
                    }
                }
            }
        }
    }

    @Test
    fun `should log properly when a throw occur during markAsExecuted`() {
        // Given
        val logger = LoggerFactory.getLogger("datamaintain.core.step.executor.Executor") as Logger
        val testAppender = TestAppender()

        logger.addAppender(testAppender)
        testAppender.start()

        every { dbDriverMock.markAsExecuted(any()) }.throws(DatamaintainQueryException(""))

        val context = Context(DatamaintainConfig(
            path = Paths.get("/default/test"),
            identifierRegex = Regex(""),
            driverConfig = FakeDriverConfig(),
            executionMode = ExecutionMode.NORMAL
        ), dbDriver = dbDriverMock)
        val executor = Executor(context)

        val scripts = listOf(
            script1.copy(action = ScriptAction.MARK_AS_EXECUTED)
        )

        val executedScript = buildExecutedScript(scripts[0], Execution(OK), context.config.flags)

        // When
        val report: Report = executor.execute(scripts)

        // Then
        expectThat(testAppender.events) {
            get { get(3).message }.contains(
                "Failed to mark script ${executedScript.name} as executed. Use the following command to force mark the script as executed : ./datamaintain-cli --db-type ${context.config.driverConfig.dbType} --db-uri ${context.config.driverConfig.uri} update-db --path ${context.config.path} --action MARK_AS_EXECUTED"
            )
        }
    }
}
