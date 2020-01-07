package datamaintain.db.driver.mongo

import datamaintain.core.config.ConfigKey
import datamaintain.core.config.getProperty
import datamaintain.core.db.driver.DatamaintainDriverConfig
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class MongoDriverConfig(val dbName: String,
                             val mongoUri: String = MongoConfigKey.DB_MONGO_URI.default!!,
                             val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                             val clientPath: Path = Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
) : DatamaintainDriverConfig {
    companion object {
        fun buildConfig(props: Properties): MongoDriverConfig {
            ConfigKey.overrideBySystemProperties(props, MongoConfigKey.values().asList())
            return MongoDriverConfig(
                    props.getProperty(MongoConfigKey.DB_MONGO_DBNAME),
                    props.getProperty(MongoConfigKey.DB_MONGO_URI),
                    props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    props.getProperty(MongoConfigKey.DB_MONGO_CLIENT_PATH).let { Paths.get(it) })
        }
    }

    override fun toDriver() = MongoDriver(
            dbName,
            mongoUri,
            tmpFilePath,
            clientPath)

    override fun log() {
        logger.info { "mongo driver configuration: " }
        logger.info { "- mongo database name -> $dbName" }
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
    DB_MONGO_URI("db.mongo.uri", "localhost:27017"),
    DB_MONGO_DBNAME("db.mongo.dbname"),
    DB_MONGO_TMP_PATH("db.mongo.tmp.path", "/tmp/datamaintain.tmp"),
    DB_MONGO_CLIENT_PATH("db.mongo.client.path", "mongo"),
}
