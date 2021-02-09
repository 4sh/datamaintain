package datamaintain.db.driver.mongo

import com.mongodb.client.model.Filters
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptAction
import datamaintain.db.driver.mongo.serialization.ExecutedScriptDb
import datamaintain.db.driver.mongo.serialization.toExecutedScriptDb
import datamaintain.db.driver.mongo.test.AbstractMongoDbTest
import org.bson.Document
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths


internal class MongoDriverTest : AbstractMongoDbTest() {
    private val mongoDatamaintainDriver = MongoDriver(
            mongoUri,
            Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
            Paths.get("mongo"),
            printOutput = false,
            saveOutput = false
    )

    @Test
    fun `should list scripts in db`() {
        // Given
        insertDataInDb()

        // When
        val executedScripts = mongoDatamaintainDriver.listExecutedScripts()

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
                ExecutionStatus.OK,
                ScriptAction.MARK_AS_EXECUTED
        )

        // When
        mongoDatamaintainDriver.markAsExecuted(script3)

        // Then
        expectThat(collection.find().toList().map { documentToExecutedScript(it) })
                .hasSize(3).and {
                    get(0).and {
                        get { name }.isEqualTo("script1.js")
                        get { checksum }.isEqualTo("c4ca4238a0b923820dcc509a6f75849b")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { action }.isEqualTo(ScriptAction.RUN)
                        get { executionOutput }.isNull()
                        get { executionDurationInMillis }.isNull()
                    }
                    get(1).and {
                        get { name }.isEqualTo("script2.js")
                        get { checksum }.isEqualTo("c81e728d9d4c2f636f067f89cc14862c")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { action }.isEqualTo(ScriptAction.RUN)
                        get { executionOutput }.isNull()
                        get { executionDurationInMillis }.isNull()
                    }
                    get(2).and {
                        get { name }.isEqualTo("script3.js")
                        get { checksum }.isEqualTo("d3d9446802a44259755d38e6d163e820")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { action }.isEqualTo(ScriptAction.MARK_AS_EXECUTED)
                        get { executionOutput }.isNull()
                        get { executionDurationInMillis }.isNull()
                    }
                }
    }

    @Test
    fun `should execute correct file script`() {
        // Given
        database.getCollection("simple").drop()
        val fileScript = FileScript(
                Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"),
                Regex("(.*)")
        )

        // When
        val execution = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList())
                .hasSize(1).and {
                    get(0).and {
                        get { getValue("data") }.isEqualTo("inserted")
                    }
                }

        expectThat(execution) {
            get { executionStatus }.isEqualTo(ExecutionStatus.OK)
            get { executionOutput }.isNull()
        }
    }

    @Test
    fun `should print output`() {
        // Given
        database.getCollection("simple").drop()
        val fileScript = FileScript(
                Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"),
                Regex("(.*)")
        )
        val mongoDatamaintainDriver = MongoDriver(
                mongoUri,
                Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                Paths.get("mongo"),
                printOutput = true,
                saveOutput = true
        )

        // When
        val executedScript = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        expectThat(executedScript) {
            get { executionOutput }.isNotNull()
        }
    }

    @Test
    fun `should save output`() {
        // Given
        val mongoDatamaintainDriver = MongoDriver(
                mongoUri,
                Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                Paths.get("mongo"),
                printOutput = false,
                saveOutput = true
        )
        val script3 = ExecutedScript(
                "script3.js",
                "d3d9446802a44259755d38e6d163e820",
                "",
                ExecutionStatus.OK,
                ScriptAction.RUN,
                0,
                executionOutput = "test"
        )

        // When
        val executedScript = mongoDatamaintainDriver.markAsExecuted(script3)

        // Then
        expectThat(executedScript) {
            get { executionOutput }.isEqualTo("test")
        }

        expectThat(collection.find().toList().map { documentToExecutedScript(it) })
                .hasSize(1).and {
                    get(0).and {
                        get { executionOutput }.isEqualTo("test")
                    }
                }
    }

    @Test
    fun `should execute correct in memory script`() {
        // Given
        database.getCollection("simple").drop()
        val content = Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js").toFile().readText()
        val inMemoryScript = InMemoryScript("test", content, "")

        // When
        val execution = mongoDatamaintainDriver.executeScript(inMemoryScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList())
                .hasSize(1).and {
                    get(0).and {
                        get { getValue("data") }.isEqualTo("inserted")
                    }
                }

        expectThat(execution) {
            get { executionStatus }.isEqualTo(ExecutionStatus.OK)
            get { executionOutput }.isNull()
        }
    }

    @Test
    fun `should execute incorrect file script`() {
        // Given
        database.getCollection("simple").drop()
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_error_insert.js"), Regex("(.*)"))

        // When
        val execution = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList()).hasSize(0)

        expectThat(execution) {
            get { executionStatus }.isEqualTo(ExecutionStatus.KO)
            get { executionOutput }.isNull()
        }
    }

    @Test
    fun `should truncate execution output when it is too big`() {
        // Given
        val mongoDatamaintainDriver = MongoDriver(
                mongoUri,
                Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                Paths.get("mongo"),
                printOutput = false,
                saveOutput = true
        )
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_print_too_many_logs.js"),
                Regex("(.*)"))

        // When
        val execution = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        expectThat(execution.executionOutput) {
            and {
                isNotNull()
                get { this!!.endsWith(MongoDriver.OUTPUT_TRUNCATED_MESSAGE) }.isTrue()
            }
        }
    }

    @Test
    fun `should not throw exception when inserting in database execution of a scripts that logs too much`() {
        // Given
        val mongoDatamaintainDriver = MongoDriver(
                mongoUri,
                Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                Paths.get("mongo"),
                printOutput = false,
                saveOutput = true
        )
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_print_too_many_logs.js"),
                Regex("(.*)"))

        // When
        val execution = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        expectCatching { mongoDatamaintainDriver.markAsExecuted(ExecutedScript.build(fileScript, execution, 0)) }
                .succeeded()
    }

    private fun insertDataInDb() {
        collection.insertMany(listOf(
                executedScriptToDocument(script1.toExecutedScriptDb()),
                executedScriptToDocument(script2.toExecutedScriptDb())
        ))
    }

    private val SCRIPT_DOCUMENT_ID = "_id"
    private val SCRIPT_DOCUMENT_NAME = "name"
    private val SCRIPT_DOCUMENT_CHECKSUM = "checksum"
    private val SCRIPT_DOCUMENT_IDENTIFIER = "identifier"
    private val SCRIPT_DOCUMENT_EXECUTION_STATUS = "executionStatus"
    private val SCRIPT_DOCUMENT_ACTION = "action"
    private val SCRIPT_DOCUMENT_EXECUTION_DURATION_IN_MILLIS = "executionDurationInMillis"
    private val SCRIPT_DOCUMENT_EXECUTION_OUTPUT = "executionOutput"

    private fun executedScriptToDocument(executedScript: ExecutedScriptDb): Document =
            Document()
                    .append(SCRIPT_DOCUMENT_ID, executedScript.id)
                    .append(SCRIPT_DOCUMENT_NAME, executedScript.name)
                    .append(SCRIPT_DOCUMENT_CHECKSUM, executedScript.checksum)
                    .append(SCRIPT_DOCUMENT_IDENTIFIER, executedScript.identifier)
                    .append(SCRIPT_DOCUMENT_EXECUTION_STATUS, executedScript.executionStatus.name)
                    .append(SCRIPT_DOCUMENT_ACTION, executedScript.action.name)
                    .append(SCRIPT_DOCUMENT_EXECUTION_DURATION_IN_MILLIS, executedScript.executionDurationInMillis)
                    .append(SCRIPT_DOCUMENT_EXECUTION_OUTPUT, executedScript.executionOutput)

    private fun documentToExecutedScript(document: Document) =
            ExecutedScript(
                    document.getString(SCRIPT_DOCUMENT_NAME),
                    document.getString(SCRIPT_DOCUMENT_CHECKSUM),
                    document.getString(SCRIPT_DOCUMENT_IDENTIFIER),
                    ExecutionStatus.valueOf(document.getString(SCRIPT_DOCUMENT_EXECUTION_STATUS)),
                    ScriptAction.valueOf(document.getString(SCRIPT_DOCUMENT_ACTION)),
                    // A getDouble is done here because JSON is used for serialization and, since mongo, by default,
                    // Reads numbers as doubles, the duration is stored as a double
                    if(document.get(SCRIPT_DOCUMENT_EXECUTION_DURATION_IN_MILLIS) != null) document.getDouble(SCRIPT_DOCUMENT_EXECUTION_DURATION_IN_MILLIS).toLong() else null,
                    document.getString(SCRIPT_DOCUMENT_EXECUTION_OUTPUT)
            )

    private val script1 = ExecutedScript(
            "script1.js",
            "c4ca4238a0b923820dcc509a6f75849b",
            "",
            ExecutionStatus.OK,
            ScriptAction.RUN
    )

    private val script2 = ExecutedScript(
            "script2.js",
            "c81e728d9d4c2f636f067f89cc14862c",
            "",
            ExecutionStatus.OK,
            ScriptAction.RUN
    )
}

