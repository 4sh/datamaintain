package datamaintain

import datamaintain.db.drivers.MongoDatamaintainDriver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.litote.kmongo.KMongo

abstract class AbstractDbTest {
    val databaseName = "datamaintain-test"
    val mongoUri = "localhost:27018"

    private val client = KMongo.createClient(mongoUri)
    private val database = client.getDatabase(databaseName)

    val collection = database.getCollection(
            MongoDatamaintainDriver.EXECUTED_SCRIPTS_COLLECTION, ScriptWithoutContent::class.java)

    @BeforeEach
    fun init() {
        cleanDb()
    }

    @AfterEach
    fun cleanDb() {
        collection.drop()
    }
}
