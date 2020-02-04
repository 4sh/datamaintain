package datamaintain.db.driver.mongo

import com.mongodb.client.model.Filters
import datamaintain.core.script.*
import datamaintain.db.driver.mongo.MongoDriver.Companion.documentToExecutedScript
import datamaintain.db.driver.mongo.MongoDriver.Companion.executedScriptToDocument
import datamaintain.db.driver.mongo.test.AbstractMongoDbTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths


internal class MongoDriverTest : AbstractMongoDbTest() {
    private val mongoDatamaintainDriver = MongoDriver(
            connectionString,
            Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
            Paths.get("mongo")
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
                ExecutionStatus.OK
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
                        get { executionOutput }.isNull()
                    }
                    get(1).and {
                        get { name }.isEqualTo("script2.js")
                        get { checksum }.isEqualTo("c81e728d9d4c2f636f067f89cc14862c")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { executionOutput }.isNull()
                    }
                    get(2).and {
                        get { name }.isEqualTo("script3.js")
                        get { checksum }.isEqualTo("d3d9446802a44259755d38e6d163e820")
                        get { identifier }.isEqualTo("")
                        get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                        get { executionOutput }.isNull()
                    }
                }
    }

    @Test
    fun `should execute correct file script`() {
        // Given
        database.getCollection("simple").drop()
        val fileScript = FileScript(
                Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"),
                Regex("")
        )

        // When
        val executedScript = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList())
                .hasSize(1).and {
                    get(0).and {
                        get { getValue("data") }.isEqualTo("inserted")
                    }
                }

        expectThat(executedScript) {
            get { name }.isEqualTo("mongo_simple_insert.js")
            get { executionStatus }.isEqualTo(ExecutionStatus.OK)
            get { executionOutput }.isNull()
        }
    }

    @Test
    fun `should execute correct in memory script`() {
        // Given
        database.getCollection("simple").drop()
        val content = Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js").toFile().readText()
        val inMemoryScript = InMemoryScript("test", content, "")

        // When
        val executedScript = mongoDatamaintainDriver.executeScript(inMemoryScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList())
                .hasSize(1).and {
                    get(0).and {
                        get { getValue("data") }.isEqualTo("inserted")
                    }
                }

        expectThat(executedScript) {
            get { name }.isEqualTo("test")
            get { executionStatus }.isEqualTo(ExecutionStatus.OK)
            get { executionOutput }.isNull()
        }
    }

    @Test
    fun `should execute incorrect file script`() {
        // Given
        database.getCollection("simple").drop()
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_error_insert.js"), Regex(""))

        // When
        val executedScript = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList()).hasSize(0)

        expectThat(executedScript) {
            get { name }.isEqualTo("mongo_error_insert.js")
            get { executionStatus }.isEqualTo(ExecutionStatus.KO)
            get { executionOutput }.isNull()
        }
    }

    private fun insertDataInDb() {
        collection.insertMany(listOf(
                executedScriptToDocument(script1),
                executedScriptToDocument(script2)
        ))
    }


    private val script1 = ExecutedScript(
            "script1.js",
            "c4ca4238a0b923820dcc509a6f75849b",
            "",
            ExecutionStatus.OK
    )

    private val script2 = ExecutedScript(
            "script2.js",
            "c81e728d9d4c2f636f067f89cc14862c",
            "",
            ExecutionStatus.OK
    )
}

