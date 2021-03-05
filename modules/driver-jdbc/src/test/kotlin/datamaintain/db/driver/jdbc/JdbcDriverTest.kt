package datamaintain.db.driver.jdbc

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ScriptAction
import org.h2.tools.Server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import strikt.assertions.size
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class JdbcDriverTest {
    private val server: Server = Server.createTcpServer().start()
    private val jdbcUri = "jdbc:h2:mem"
    private val connection: Connection = DriverManager.getConnection(jdbcUri)
    private val jdbcDatamaintainDriver = JdbcDriver(
            jdbcUri,
            Paths.get(JdbcConfigKey.DB_JDBC_TMP_PATH.default!!),
            Paths.get("jdbc"),
            printOutput = false,
            saveOutput = false
    )

    @BeforeAll
    fun `create executed scripts table`() {
        jdbcDatamaintainDriver.createExecutedScriptsTableIfNotExists()
    }

    @AfterAll
    fun `stop h2 database`() {
        connection.prepareStatement("""DROP TABLE ${JdbcDriver.EXECUTED_SCRIPTS_TABLE}""").execute()
        this.server.stop()
    }

    @Test
    fun `should list scripts in db`() {
        // Given
        insertDataInDb()

        // When
        val executedScripts = jdbcDatamaintainDriver.listExecutedScripts()

        // Then
        expectThat(executedScripts.toList()) {
            size.isEqualTo(2)
            contains(script1, script2)
        }
    }

    @Test
    fun `should mark script as executed`() {
        // Given
        insertDataInDb()
        val script3 = ExecutedScript(
                "script3.js",
                "d3d9446802a44259755d38e6d163e820",
                "",
                ExecutionStatus.OK
        )

        // When
        jdbcDatamaintainDriver.markAsExecuted(script3)

        // Then
        expectThat(collection.find().toList().map { documentToExecutedScript(it) })
                .hasSize(3).and {
                    get(0).and {
                        get { name }.isEqualTo("script1.js")
                        get { checksum }.isEqualTo("c4ca4238a0b923820dcc509a6f75849b")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { executionOutput }.isNull()
                        get { executionDurationInMillis }.isNull()
                    }
                    get(1).and {
                        get { name }.isEqualTo("script2.js")
                        get { checksum }.isEqualTo("c81e728d9d4c2f636f067f89cc14862c")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { executionOutput }.isNull()
                        get { executionDurationInMillis }.isNull()
                    }
                    get(2).and {
                        get { name }.isEqualTo("script3.js")
                        get { checksum }.isEqualTo("d3d9446802a44259755d38e6d163e820")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { executionOutput }.isNull()
                        get { executionDurationInMillis }.isNull()
                    }
                }
    }
//
//    @Test
//    fun `should execute correct file script`() {
//        // Given
//        database.getCollection("simple").drop()
//        val fileScript = FileScript(
//                Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"),
//                Regex("(.*)")
//        )
//
//        // When
//        val execution = jdbcDatamaintainDriver.executeScript(fileScript)
//
//        // Then
//        val coll = database.getCollection("simple")
//        val cursor = coll.find(Filters.eq("find", "me"))
//        expectThat(cursor.toList())
//                .hasSize(1).and {
//                    get(0).and {
//                        get { getValue("data") }.isEqualTo("inserted")
//                    }
//                }
//
//        expectThat(execution) {
//            get { executionStatus }.isEqualTo(ExecutionStatus.OK)
//            get { executionOutput }.isNull()
//        }
//    }
//
//    @Test
//    fun `should print output`() {
//        // Given
//        database.getCollection("simple").drop()
//        val fileScript = FileScript(
//                Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"),
//                Regex("(.*)")
//        )
//        val mongoDatamaintainDriver = JdbcDriver(
//                mongoUri,
//                Paths.get(JdbcConfigKey.DB_JDBC_TMP_PATH.default!!),
//                Paths.get("mongo"),
//                printOutput = true,
//                saveOutput = true
//        )
//
//        // When
//        val executedScript = mongoDatamaintainDriver.executeScript(fileScript)
//
//        // Then
//        expectThat(executedScript) {
//            get { executionOutput }.isNotNull()
//        }
//    }
//
//    @Test
//    fun `should save output`() {
//        // Given
//        val mongoDatamaintainDriver = JdbcDriver(
//                mongoUri,
//                Paths.get(JdbcConfigKey.DB_JDBC_TMP_PATH.default!!),
//                Paths.get("mongo"),
//                printOutput = false,
//                saveOutput = true
//        )
//        val script3 = ExecutedScript(
//                "script3.js",
//                "d3d9446802a44259755d38e6d163e820",
//                "",
//                ExecutionStatus.OK,
//                0,
//                executionOutput = "test"
//        )
//
//        // When
//        val executedScript = mongoDatamaintainDriver.markAsExecuted(script3)
//
//        // Then
//        expectThat(executedScript) {
//            get { executionOutput }.isEqualTo("test")
//        }
//
//        expectThat(collection.find().toList().map { documentToExecutedScript(it) })
//                .hasSize(1).and {
//                    get(0).and {
//                        get { executionOutput }.isEqualTo("test")
//                    }
//                }
//    }
//
//    @Test
//    fun `should execute correct in memory script`() {
//        // Given
//        database.getCollection("simple").drop()
//        val content = Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js").toFile().readText()
//        val inMemoryScript = InMemoryScript("test", content, "")
//
//        // When
//        val execution = jdbcDatamaintainDriver.executeScript(inMemoryScript)
//
//        // Then
//        val coll = database.getCollection("simple")
//        val cursor = coll.find(Filters.eq("find", "me"))
//        expectThat(cursor.toList())
//                .hasSize(1).and {
//                    get(0).and {
//                        get { getValue("data") }.isEqualTo("inserted")
//                    }
//                }
//
//        expectThat(execution) {
//            get { executionStatus }.isEqualTo(ExecutionStatus.OK)
//            get { executionOutput }.isNull()
//        }
//    }
//
//    @Test
//    fun `should execute incorrect file script`() {
//        // Given
//        database.getCollection("simple").drop()
//        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_error_insert.js"), Regex("(.*)"))
//
//        // When
//        val execution = jdbcDatamaintainDriver.executeScript(fileScript)
//
//        // Then
//        val coll = database.getCollection("simple")
//        val cursor = coll.find(Filters.eq("find", "me"))
//        expectThat(cursor.toList()).hasSize(0)
//
//        expectThat(execution) {
//            get { executionStatus }.isEqualTo(ExecutionStatus.KO)
//            get { executionOutput }.isNull()
//        }
//    }

    private fun insertDataInDb() {
        connection.prepareStatement("""
            INSERT INTO ${JdbcDriver.EXECUTED_SCRIPTS_TABLE} (
                id, `name`, checksum, identifier, executionStatus, `action`
            ) VALUES 
            ('id1', 'script1.sql', 'c4ca4238a0b923820dcc509a6f75849b', '', 'OK', 'RUN'), 
            ('id2', 'script2.sql', 'c81e728d9d4c2f636f067f89cc14862c', '', 'OK', 'RUN')
        """).execute()
    }

    private val SCRIPT_DOCUMENT_ID = "_id"
    private val SCRIPT_DOCUMENT_NAME = "name"
    private val SCRIPT_DOCUMENT_CHECKSUM = "checksum"
    private val SCRIPT_DOCUMENT_IDENTIFIER = "identifier"
    private val SCRIPT_DOCUMENT_EXECUTION_STATUS = "executionStatus"
    private val SCRIPT_DOCUMENT_EXECUTION_DURATION_IN_MILLIS = "executionDurationInMillis"
    private val SCRIPT_DOCUMENT_EXECUTION_OUTPUT = "executionOutput"

    private val script1 = ExecutedScript(
            "script1.sql",
            "c4ca4238a0b923820dcc509a6f75849b",
            "",
            ExecutionStatus.OK,
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
}

