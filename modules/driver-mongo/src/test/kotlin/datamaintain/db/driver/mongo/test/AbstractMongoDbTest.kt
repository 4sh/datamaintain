package datamaintain.db.driver.mongo.test

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClients
import datamaintain.db.driver.mongo.MongoDriver
import org.bson.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractMongoDbTest {
    val databaseName = "datamaintain-test"
    val mongoHost = "localhost:27018"
    val mongoUri = "mongodb://localhost:27018/datamaintain-test"
    val connectionString = ConnectionString(mongoUri)

    private val client = MongoClients.create(connectionString)
    val database = client.getDatabase(databaseName)

    val collection = database.getCollection(
            MongoDriver.EXECUTED_SCRIPTS_COLLECTION, Document::class.java)

    @BeforeEach
    fun init() {
        cleanDb()
    }

    @AfterEach
    fun cleanDb() {
//        collection.drop()
//        database.getCollection("simple").drop()
    }
}
