package datamaintain.db.driver.mongo.test

import datamaintain.db.driver.mongo.MongoDriver
import com.mongodb.client.MongoClients
import org.bson.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractMongoDbTest {
    val databaseName = "datamaintain-test"
    val mongoHost = "localhost:27018"
    val mongoUri = "mongodb://localhost:27018/datamaintain-test"

    private val client = MongoClients.create(mongoUri)
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
