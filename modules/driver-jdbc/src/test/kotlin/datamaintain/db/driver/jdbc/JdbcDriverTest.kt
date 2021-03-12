package datamaintain.db.driver.jdbc

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptAction
import org.h2.tools.Server
import org.junit.jupiter.api.*
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class JdbcDriverTest {
    private val server: Server = Server.createTcpServer().start()
    private val jdbcUri = "jdbc:h2:mem"
    private val connection: Connection = DriverManager.getConnection(jdbcUri)
    private val jdbcDatamaintainDriver = JdbcDriver(jdbcUri)

    @BeforeEach
    fun `create executed scripts table`() {
        jdbcDatamaintainDriver.createExecutedScriptsTableIfNotExists()
    }

    @AfterEach
    fun `drop table`() {
        connection.prepareStatement("""DROP TABLE ${JdbcDriver.EXECUTED_SCRIPTS_TABLE}""").execute()
    }

    @AfterAll
    fun `stop h2 database`() {
        this.server.stop()
    }

    @Test
    fun `should list scripts in db`() {
        // Given
        insertDataInDb()

        // When
        val executedScripts = jdbcDatamaintainDriver.listExecutedScripts()

        // Then
        expectThat(executedScripts.toList()).containsExactlyInAnyOrder(script1, script2)
    }

    @Test
    fun `should mark script as executed`() {
        // Given
        insertDataInDb()
        val script3 = ExecutedScript(
                "script3.sql",
                "d3d9446802a44259755d38e6d163e820",
                "",
                ExecutionStatus.OK,
                ScriptAction.MARK_AS_EXECUTED,
                executionDurationInMillis = 0
        )

        // When
        jdbcDatamaintainDriver.markAsExecuted(script3)

        // Then

        expectThat(jdbcDatamaintainDriver.listExecutedScripts().toList())
                .containsExactlyInAnyOrder(script1, script2, script3)
    }

    @Nested
    inner class ExecuteScript {
        @AfterEach()
        fun dropTableIfExists() {
            connection.prepareStatement("DROP TABLE IF EXISTS crystalDevs").execute()
        }

        @Test
        fun `should execute correct file script`() {
            // Given
            val fileScript = FileScript(
                    Paths.get("src/test/resources/executor_test_files/jdbc/sql_simple_insert.sql"),
                    Regex("(.*)")
            )

            // When
            val execution = jdbcDatamaintainDriver.executeScript(fileScript)

            // Then
            expectThat(findCrystalDevs()).containsExactly("Elise", "Tom")

            expectThat(execution) {
                get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                get { executionOutput }.isNull()
            }
        }

        @Test
        fun `should execute incorrect file script`() {
            // Given
            val fileScript = FileScript(
                    Paths.get("src/test/resources/executor_test_files/jdbc/sql_error_insert.sql"),
                    Regex("(.*)"))

            // When
            val execution = jdbcDatamaintainDriver.executeScript(fileScript)

            // Then
            expectThat(execution) {
                get { executionStatus }.isEqualTo(ExecutionStatus.KO)
                get { executionOutput }.isEqualTo("Table \"CRYSTALDEVSERROR\" not found; SQL statement:\n" +
                        "INSERT INTO crystalDevsError VALUES ('Elise'), ('Tom'); [42102-148]")
            }
        }

        private fun findCrystalDevs(): List<String> =
                connection.prepareStatement("SELECT * FROM crystalDevs").executeQuery().toCrystalDevs()

        private fun ResultSet.toCrystalDevs(): List<String> {
            val crystalDevs = mutableListOf<String>()

            while (this.next()) {
                crystalDevs.add(this.getString("firstName"))
            }

            return crystalDevs
        }
    }

    @Test
    fun `should override script`() {
        // Given
        insertDataInDb()
        val script3 = script1.copy(checksum = "8747e564eb53cb2f1dcb9aae0779c2aa",
                executionStatus = ExecutionStatus.OK,
                action = ScriptAction.OVERRIDE_EXECUTED)

        // When
        jdbcDatamaintainDriver.overrideScript(script3)

        // Then
        expectThat(jdbcDatamaintainDriver.listExecutedScripts().toList())
                .containsExactlyInAnyOrder(
                        script3,
                        script2
                )
    }

    private val script1 = ExecutedScript(
            "script1.sql",
            "c4ca4238a0b923820dcc509a6f75849b",
            "",
            ExecutionStatus.KO,
            ScriptAction.RUN,
            0
    )

    private val script2 = ExecutedScript(
            "script2.sql",
            "c81e728d9d4c2f636f067f89cc14862c",
            "",
            ExecutionStatus.OK,
            ScriptAction.RUN,
            0
    )

    private fun insertDataInDb() {
        connection.prepareStatement("""
            INSERT INTO ${JdbcDriver.EXECUTED_SCRIPTS_TABLE} (
                id, `name`, checksum, identifier, executionStatus, `action`
            ) VALUES 
            ('id1', '${script1.name}', '${script1.checksum}', '${script1.identifier}', '${script1.executionStatus}', '${script1.action}'), 
            ('id2', '${script2.name}', '${script2.checksum}', '${script2.identifier}', '${script2.executionStatus}', '${script2.action}')
        """).execute()
    }

}

