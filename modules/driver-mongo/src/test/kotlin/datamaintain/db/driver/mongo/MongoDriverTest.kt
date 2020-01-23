package datamaintain.db.driver.mongo

import com.mongodb.client.model.Filters
import datamaintain.core.report.ExecutionStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.ScriptWithoutContent
import datamaintain.core.script.Tag
import datamaintain.db.driver.mongo.test.AbstractMongoDbTest
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.math.BigInteger
import java.nio.file.Paths
import java.security.MessageDigest


internal class MongoDriverTest : AbstractMongoDbTest() {
    private val mongoDatamaintainDriver = MongoDriver(
            mongoUri,
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
        val script3 = ScriptWithoutContent("script3.js", "d3d9446802a44259755d38e6d163e820", "")

        // When
        mongoDatamaintainDriver.markAsExecuted(script3)

        // Then
        val executedScripts = collection.find().toList().map { mongoDatamaintainDriver.documentToScriptWithoutContent(it) }
        expectThat(executedScripts.toList()) {
            size.isEqualTo(3)
            contains(script1, script2, script3)
        }
    }

    @Test
    fun `should execute correct file script`() {
        // Given
        database.getCollection("simple").drop()
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"), Regex(""))

        // When
        val report = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList())
                .hasSize(1).and {
                    get(0).and {
                        get { getValue("data") }.isEqualTo("inserted")
                    }
                }

        expectThat(report) {
            get { message }.isEmpty()
        }
    }

    @Test
    fun `should execute correct in memory script`() {
        // Given
        database.getCollection("simple").drop()
        val content = Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js").toFile().readText()
        val inMemoryScript = InMemoryScript("test", content, "")

        // When
        val report = mongoDatamaintainDriver.executeScript(inMemoryScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList())
                .hasSize(1).and {
                    get(0).and {
                        get { getValue("data") }.isEqualTo("inserted")
                    }
                }

        expectThat(report) {
            get { message }.isEmpty()
        }
    }

    @Test
    fun `should execute incorrect file script`() {
        // Given
        database.getCollection("simple").drop()
        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_error_insert.js"), Regex(""))

        // When
        val report = mongoDatamaintainDriver.executeScript(fileScript)

        // Then
        val coll = database.getCollection("simple")
        val cursor = coll.find(Filters.eq("find", "me"))
        expectThat(cursor.toList()).hasSize(0)

        expectThat(report) {
            get { message }.contains("failed to load: src/test/resources/executor_test_files/mongo/mongo_error_insert.js")
        }
    }

    @Test
    fun `should throw a mongoUri protocol missing`() {
        // mongo host has form host:port and miss mongodb protocol
        expectCatching { MongoDriver(
                mongoHost,
                Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
        ) }
                .failed()
                .isA<java.lang.IllegalArgumentException>()
                .get { localizedMessage }.contains("Connection strings must start with either 'mongodb://' or 'mongodb+srv://")
    }

    @Test
    fun `should throw a collection error in mongoURI`() {
        // mongo host has form host:port and miss mongodb protocol
        expectCatching { MongoDriver(
                "${mongoUri}.aCollection",
                Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
        ) }
                .failed()
                .isA<java.lang.IllegalArgumentException>()
                .get { localizedMessage }.contains("MongoUri contains a collection name, please remove it")
    }

    @Test
    fun `should work with parameterized mongoURI`() {
        // mongo host has form host:port and miss mongodb protocol
        val mongoDriver = MongoDriver(
                "${mongoUri}?connectTimeoutMS=500000",
                Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
        )

        val fileScript = FileScript(Paths.get("src/test/resources/executor_test_files/mongo/mongo_simple_insert.js"), Regex(""))

        // When
        val report = mongoDriver.executeScript(fileScript)

        expectThat(report) {
            get { executionStatus }.isEqualTo(ExecutionStatus.OK)
        }
    }

    private fun insertDataInDb() {
        collection.insertMany(listOf(
                mongoDatamaintainDriver.scriptWithoutContentToDocument(script1),
                mongoDatamaintainDriver.scriptWithoutContentToDocument(script2)
        ))
    }


    private val script1 = ScriptWithoutContent("script1.js", "c4ca4238a0b923820dcc509a6f75849b", "")
    private val script2 = ScriptWithoutContent("script2.js", "c81e728d9d4c2f636f067f89cc14862c", "")
}

class InMemoryScript(
        override val name: String,
        override val content: String,
        override val identifier: String,
        override val tags: Set<Tag> = setOf()) : ScriptWithContent {

    override val checksum: String by lazy {
        content.hash()
    }

    private fun String.hash(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}