package datamaintain.db.driver.mongo.test

import datamaintain.core.script.ScriptWithoutContent
import datamaintain.db.driver.mongo.MongoDriver
import com.mongodb.client.MongoClients
import org.bson.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractMongoDbTest {
    val databaseName = "datamaintain-test"
    val mongoUri = "localhost:27018"

    private val client = MongoClients.create("mongodb://$mongoUri")
    val database = client.getDatabase(databaseName)

    val collection = database.getCollection(
            MongoDriver.EXECUTED_SCRIPTS_COLLECTION, Document::class.java)

    @BeforeEach
    fun init() {
        cleanDb()
    }

    @AfterEach
    fun cleanDb() {
        collection.drop()
    }
}
