package datamaintain.db.driver.mongo

import com.mongodb.ConnectionString
import datamaintain.core.config.ConfigKey
import datamaintain.core.config.getProperty
import datamaintain.core.db.driver.DatamaintainDriverConfig
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class MongoDriverConfig(val mongoUri: String,
                             val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                             val clientPath: Path = Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
) : DatamaintainDriverConfig {
    companion object {
        @JvmStatic
        fun buildConfig(props: Properties): MongoDriverConfig {
            ConfigKey.overrideBySystemProperties(props, MongoConfigKey.values().asList())
            return MongoDriverConfig(
                    props.getProperty(MongoConfigKey.DB_MONGO_URI),
                    props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    props.getProperty(MongoConfigKey.DB_MONGO_CLIENT_PATH).let { Paths.get(it) })
        }
    }

    override fun toDriver() = MongoDriver(
            buildConnectionString(mongoUri),
            tmpFilePath,
            clientPath)

    private fun buildConnectionString(mongoUri: String): ConnectionString {
        val connectionString = ConnectionString(mongoUri)
        // mongoUri can come with a database but currently driver's dbName is mandatory
        if (connectionString.database == null) {
            throw IllegalArgumentException("MongoUri does not contains a database name")
        }

        // mongoUri can come with a collection. It has no sense in DataMaintain's logic
        if (connectionString.collection != null) {
            throw IllegalArgumentException("MongoUri contains a collection name, please remove it")
        }
        return connectionString
    }

    override fun log() {
        logger.info { "Mongo driver configuration: " }
        logger.info { "- mongo uri -> $mongoUri" }
        logger.info { "- mongo tmp file -> $tmpFilePath" }
        logger.info { "- mongo client -> $clientPath" }
        logger.info { "" }
    }
}

enum class MongoConfigKey(
        override val key: String,
        override val default: String? = null
) : ConfigKey {
    DB_MONGO_URI("db.mongo.uri"),
    DB_MONGO_TMP_PATH("db.mongo.tmp.path", "/tmp/datamaintain.tmp"),
    DB_MONGO_CLIENT_PATH("db.mongo.client.path", "mongo"),
}
