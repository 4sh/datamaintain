package datamaintain.test

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import datamaintain.core.db.driver.DriverConfigKey
import org.bson.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class AbstractMongoDbTest {
    val mongoTag = "mongo:5.0"
    val databaseName = "datamaintain-test"

    @Container
    val mongoDBContainer: MongoDBContainer = MongoDBContainer(mongoTag)

    // Fill when mongo container is up
    private var mongoUri: String? = null
    private var connectionString: ConnectionString? = null
    private var client: MongoClient? = null
    private var database: MongoDatabase? = null
    private var collection: MongoCollection<Document>? = null

    fun mongoUri(): String {
        return mongoUri!!
    }

    fun database(): MongoDatabase {
        return database!!
    }

    fun collection(): MongoCollection<Document> {
        return collection!!
    }

    @BeforeEach
    fun init() {
        // Fetch URI of container and start a client on it
        mongoUri = mongoDBContainer.getReplicaSetUrl(databaseName)
        connectionString = ConnectionString(mongoUri!!)
        client = MongoClients.create(connectionString!!)
        database = client!!.getDatabase(databaseName)
        collection = database!!.getCollection(DriverConfigKey.EXECUTED_SCRIPTS_STORAGE_NAME.default!!, Document::class.java)
    }

    @AfterEach
    fun cleanDbConnection() {
        // Close mongo client and clean fields
        client!!.close()
        mongoUri = null
        connectionString = null
        client = null
        database = null
        collection = null
    }
}
