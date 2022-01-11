package datamaintain.db.driver.mongo

import com.mongodb.client.model.Filters
import datamaintain.core.script.*
import datamaintain.db.driver.mongo.serialization.ExecutedScriptDb
import datamaintain.db.driver.mongo.serialization.toExecutedScriptDb
import datamaintain.db.driver.mongo.test.AbstractMongoDbTest
import org.bson.Document
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Path
import java.nio.file.Paths


internal class MongoDriverTest : AbstractMongoDbTest() {
    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should list scripts in db`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell)
        insertDataInDb()

        // When
        val executedScripts = mongoDriver.listExecutedScripts()

        // Then
        expectThat(executedScripts.toList()) {
            size.isEqualTo(2)
            contains(script1.toLightExecutedScript(), script2.toLightExecutedScript())
        }
    }

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should mark script as executed`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell)
        insertDataInDb()
        val script3 = ExecutedScript(
                "script3.js",
                "d3d9446802a44259755d38e6d163e820",
                "",
                ExecutionStatus.OK,
                ScriptAction.MARK_AS_EXECUTED
        )

        // When
        mongoDriver.markAsExecuted(script3)

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

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should override script`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell)
        insertDataInDb()
        val script3 = ExecutedScript(
                "script1.js",
                "8747e564eb53cb2f1dcb9aae0779c2aa",
                "",
                ExecutionStatus.OK,
                ScriptAction.OVERRIDE_EXECUTED
        )

        // When
        mongoDriver.overrideScript(script3)

        // Then
        expectThat(collection.find().toList().map { documentToExecutedScript(it) })
                .hasSize(2).and {
                    get(0).and {
                        get { name }.isEqualTo("script1.js")
                        get { checksum }.isEqualTo("8747e564eb53cb2f1dcb9aae0779c2aa")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { action }.isEqualTo(ScriptAction.OVERRIDE_EXECUTED)
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
                }
    }

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should mark script as executed with flags`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell)
        insertDataInDb()
        val script3 = ExecutedScript(
            "script3.js",
            "d3d9446802a44259755d38e6d163e820",
            "",
            ExecutionStatus.OK,
            ScriptAction.MARK_AS_EXECUTED,
            flags = listOf("FLAG1", "FLAG1", "FLAG1")
        )

        // When
        mongoDriver.markAsExecuted(script3)

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
                    get { flags }.isEmpty()
                }
                get(1).and {
                    get { name }.isEqualTo("script2.js")
                    get { checksum }.isEqualTo("c81e728d9d4c2f636f067f89cc14862c")
                    get { identifier }.isEqualTo("")
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { action }.isEqualTo(ScriptAction.RUN)
                    get { executionOutput }.isNull()
                    get { executionDurationInMillis }.isNull()
                    get { flags }.isEmpty()
                }
                get(2).and {
                    get { name }.isEqualTo("script3.js")
                    get { checksum }.isEqualTo("d3d9446802a44259755d38e6d163e820")
                    get { identifier }.isEqualTo("")
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { action }.isEqualTo(ScriptAction.MARK_AS_EXECUTED)
                    get { executionOutput }.isNull()
                    get { executionDurationInMillis }.isNull()
                    get { flags }.and {
                        hasSize(3)
                        containsExactly("FLAG1", "FLAG1", "FLAG1")
                    }
                }
            }
    }

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should execute correct file script`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell)
        database.getCollection("simple").drop()
        val fileScript = FileScript(
                Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"),
                Regex("(.*)")
        )

        // When
        val execution = mongoDriver.executeScript(fileScript)

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

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should print output`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(
            mongoShell,
            printOutput = true,
            saveOutput = true
        )
        database.getCollection("simple").drop()
        val fileScript = FileScript(
                Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"),
                Regex("(.*)")
        )

        // When
        val executedScript = mongoDriver.executeScript(fileScript)

        // Then
        expectThat(executedScript) {
            get { executionOutput }.isNotNull()
        }
    }

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should save output`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(
            mongoShell,
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
        val executedScript = mongoDriver.markAsExecuted(script3)

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

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should execute correct in memory script`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell)
        database.getCollection("simple").drop()
        val content = Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js").toFile().readText()
        val inMemoryScript = InMemoryScript("test", content, "")

        // When
        val execution = mongoDriver.executeScript(inMemoryScript)

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

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should execute incorrect file script`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell)
        database.getCollection("simple").drop()
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_error_insert.js"), Regex("(.*)"))

        // When
        val execution = mongoDriver.executeScript(fileScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList()).hasSize(0)

        expectThat(execution) {
            get { executionStatus }.isEqualTo(ExecutionStatus.KO)
            get { executionOutput }.isNull()
        }
    }

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should truncate execution output when it is too big`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell, saveOutput = true)
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_print_too_many_logs.js"),
                Regex("(.*)"))

        // When
        val execution = mongoDriver.executeScript(fileScript)

        // Then
        expectThat(execution.executionOutput) {
            and {
                isNotNull()
                get { this!!.endsWith(MongoDriver.OUTPUT_TRUNCATED_MESSAGE) }.isTrue()
            }
        }
    }

    @ParameterizedTest
    @EnumSource(value = MongoShell::class)
    fun `should not throw exception when inserting in database execution of a scripts that logs too much`(mongoShell: MongoShell) {
        // Given
        val mongoDriver: MongoDriver = buildMongoDriver(mongoShell, saveOutput = true)
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_print_too_many_logs.js"),
                Regex("(.*)"))

        // When
        val execution = mongoDriver.executeScript(fileScript)

        // Then
        expectCatching { mongoDriver.markAsExecuted(ExecutedScript.build(fileScript, execution, 0, listOf())) }
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
    private val SCRIPT_DOCUMENT_FLAGS = "flags"

    private fun executedScriptToDocument(executedScript: ExecutedScriptDb): Document =
            Document()
                    .append(SCRIPT_DOCUMENT_ID, executedScript.id)
                    .append(SCRIPT_DOCUMENT_NAME, executedScript.name)
                    .append(SCRIPT_DOCUMENT_CHECKSUM, executedScript.checksum)
                    .append(SCRIPT_DOCUMENT_IDENTIFIER, executedScript.identifier)
                    .append(SCRIPT_DOCUMENT_EXECUTION_STATUS, executedScript.executionStatus.name)
                    .append(SCRIPT_DOCUMENT_ACTION, executedScript.action!!.name)
                    .append(SCRIPT_DOCUMENT_EXECUTION_DURATION_IN_MILLIS, executedScript.executionDurationInMillis)
                    .append(SCRIPT_DOCUMENT_EXECUTION_OUTPUT, executedScript.executionOutput)
                    .append(SCRIPT_DOCUMENT_FLAGS, executedScript.flags)

    private fun documentToExecutedScript(document: Document): ExecutedScript {
        val executionDurationInMillis: Long? =
            document[SCRIPT_DOCUMENT_EXECUTION_DURATION_IN_MILLIS]
                ?.let {
                    when (it) {
                        is Double -> it.toLong()
                        is Int -> it.toLong()
                        else -> throw IllegalStateException("")
                    }
                }

        return ExecutedScript(
            document.getString(SCRIPT_DOCUMENT_NAME),
            document.getString(SCRIPT_DOCUMENT_CHECKSUM),
            document.getString(SCRIPT_DOCUMENT_IDENTIFIER),
            ExecutionStatus.valueOf(document.getString(SCRIPT_DOCUMENT_EXECUTION_STATUS)),
            ScriptAction.valueOf(document.getString(SCRIPT_DOCUMENT_ACTION)),
            executionDurationInMillis,
            document.getString(SCRIPT_DOCUMENT_EXECUTION_OUTPUT),
            document.getList(SCRIPT_DOCUMENT_FLAGS, String::class.java)
        )
    }

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

    private fun buildMongoDriver(mongoShell: MongoShell,
                                 printOutput: Boolean = false,
                                 saveOutput: Boolean = false,
                                 clientPath: Path? = null): MongoDriver {
        val mongoClientPath = clientPath ?: Paths.get(mongoShell.defaultBinaryName())

        return MongoDriver(
            mongoUri,
            tmpFilePath = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
            clientPath = mongoClientPath,
            saveOutput = saveOutput,
            printOutput = printOutput,
            mongoShell = mongoShell
        )
    }
}

private fun ExecutedScript.toLightExecutedScript(): LightExecutedScript = LightExecutedScript(name, checksum, identifier)

