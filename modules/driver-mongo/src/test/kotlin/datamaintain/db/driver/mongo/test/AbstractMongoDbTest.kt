package datamaintain.db.driver.mongo.test

import datamaintain.core.script.ScriptWithoutContent
import datamaintain.db.driver.mongo.MongoDriver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.litote.kmongo.KMongo

abstract class AbstractMongoDbTest {
    val databaseName = "datamaintain-test"
    val mongoUri = "localhost:27018"

    private val client = KMongo.createClient(mongoUri)
    val database = client.getDatabase(databaseName)

    val collection = database.getCollection(
            MongoDriver.EXECUTED_SCRIPTS_COLLECTION, ScriptWithoutContent::class.java)

    @BeforeEach
    fun init() {
        cleanDb()
    }

    @AfterEach
    fun cleanDb() {
        collection.drop()
    }
}
