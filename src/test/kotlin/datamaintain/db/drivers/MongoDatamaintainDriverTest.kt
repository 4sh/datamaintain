package datamaintain.db.drivers

import datamaintain.ScriptWithoutContent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.KMongo
import strikt.api.expectThat
import strikt.assertions.first
import strikt.assertions.isEqualTo
import strikt.assertions.last
import strikt.assertions.size


internal class MongoDatamaintainDriverTest {
    private val databaseName = "datamaintain-test"

    private val client = KMongo.createClient()
    private val database = client.getDatabase(databaseName)
    private val collection = database.getCollection(
            MongoDatamaintainDriver.EXECUTED_SCRIPTS_COLLECTION, ScriptWithoutContent::class.java)

    private val mongoDatamaintainDriver = MongoDatamaintainDriver(databaseName)

    @BeforeEach
    fun init() {
        cleanDb()
    }

    @AfterEach
    fun cleanDb() {
        collection.drop()
    }

    @Test
    fun `should list scripts in db`() {
        // Given
        insertDataInDb()

        // When
        val executedScripts = mongoDatamaintainDriver.listExecutedScripts()

        // Then
        expectThat(executedScripts.toList()) {
            size.isEqualTo(2)
            first().isEqualTo(script1)
            last().isEqualTo(script2)
        }
    }

    private fun insertDataInDb() {
        collection.insertMany(listOf(
                script1,
                script2
        ))
    }

    private val script1 = ScriptWithoutContent("script1.js", "c4ca4238a0b923820dcc509a6f75849b")
    private val script2 = ScriptWithoutContent("script2.js", "c81e728d9d4c2f636f067f89cc14862c")
}