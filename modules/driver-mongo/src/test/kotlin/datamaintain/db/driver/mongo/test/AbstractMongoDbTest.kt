package datamaintain.db.driver.mongo.test

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import datamaintain.db.driver.mongo.MongoDriver
import datamaintain.db.driver.mongo.MongoShell
import org.bson.Document
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.provider.Arguments
import org.testcontainers.containers.MongoDBContainer

abstract class AbstractMongoDbTest {
    private val defaultDatabaseName = "datamaintain-test"

    // Fill when mongo container is up
    private lateinit var currentMongoContainer: MongoDBContainer
    private lateinit var mongoUri: String
    private lateinit var connectionString: ConnectionString
    private lateinit var client: MongoClient
    private lateinit var database: MongoDatabase
    private lateinit var collection: MongoCollection<Document>

    /**
     * MongoUri of current mongo container
     */
    fun mongoUri(): String {
        return mongoUri
    }

    /**
     * Database of current mongo container
     */
    fun database(): MongoDatabase {
        return database
    }

    /**
     * Collection of current mongo container
     */
    fun collection(): MongoCollection<Document> {
        return collection
    }

    @AfterEach
    fun cleanDbConnection() {
        client.close()
    }

    protected fun cleanDb() {
        collection.drop()
        database.getCollection("simple").drop()
    }

    protected fun initMongoConnection(tag: String) {
        // Fetch mongoContainer and fill uri, client, etc
        currentMongoContainer = containerByTag[tag]!!
        mongoUri = currentMongoContainer.getReplicaSetUrl(defaultDatabaseName)
        connectionString = ConnectionString(mongoUri)
        client = MongoClients.create(connectionString)
        database = client.getDatabase(defaultDatabaseName)
        collection = database.getCollection(MongoDriver.EXECUTED_SCRIPTS_COLLECTION, Document::class.java)
        cleanDb()
    }

    companion object {
        // List of supported mongo version
        private val mongoVersionSpecs = listOf(
            MongoVersionSpec("mongo:4.0"),
            MongoVersionSpec("mongo:4.2"),
            MongoVersionSpec("mongo:4.4"),
            MongoVersionSpec("mongo:5.0")
        )

        // Mongo containers cache
        private var containerByTag: Map<String, MongoDBContainer> = LinkedHashMap()

        /**
         * Map MongoVersionSpec to multiples test's argument
         */
        @JvmStatic
        fun provideMongoVersions(): List<Arguments> {
            return mongoVersionSpecs
                .flatMap { it.toMongoArgument() }
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            // Start a container by mongo version
            print("Starting containers")
            val containerByTag: MutableMap<String, MongoDBContainer> = mutableMapOf()
            mongoVersionSpecs
                .forEach {
                    val mongoDBContainer = MongoDBContainer(it.tag)
                    containerByTag[it.tag] = mongoDBContainer
                    mongoDBContainer.start()
                }
            this.containerByTag = containerByTag.toMap()
            print("Started")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            // Clear all containers
            print("Stopping containers")
            containerByTag.values.forEach { it.stop() }
            print("Stopped")
        }
    }

    class MongoVersionSpec(
        val tag: String,
        val mongoCli: Boolean = true,
        val mongoSh: Boolean = true
    ) {
        /**
         * Map to multiple Argument object for represent each supported mongo connection
         */
        fun toMongoArgument(): List<Arguments> {
            val args: MutableList<Arguments> = mutableListOf()
            if (mongoCli) {
                args.add(Arguments.of(tag, MongoShell.MONGO))
            }

            if (mongoSh) {
                args.add(Arguments.of(tag, MongoShell.MONGOSH))
            }
            return args.toList()
        }
    }
}
