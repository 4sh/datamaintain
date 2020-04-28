package datamaintain.db.driver.mongo

import datamaintain.core.config.ConfigKey
import datamaintain.core.config.getProperty
import datamaintain.core.db.driver.DatamaintainDriverConfig
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class MongoDriverConfig @JvmOverloads constructor(val mongoUri: String,
                                                       val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                                                       val clientPath: Path = Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!),
                                                       val printOutput: Boolean = MongoConfigKey.DB_MONGO_PRINT_OUTPUT.default!!.toBoolean(),
                                                       val saveOutput: Boolean = MongoConfigKey.DB_MONGO_SAVE_OUTPUT.default!!.toBoolean()
) : DatamaintainDriverConfig {
    companion object {
        private val MONGO_URI_REGEX = Regex("^(mongodb)(\\+srv)?://([-_.\\w]+)?(:[\\w]+)?(@([.\\w]+):(\\d+))?/([-_\\w]+)\$")

        @JvmStatic
        fun buildConfig(props: Properties): MongoDriverConfig {
            ConfigKey.overrideBySystemProperties(props, MongoConfigKey.values().asList())
            return MongoDriverConfig(
                    props.getProperty(MongoConfigKey.DB_MONGO_URI),
                    props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    props.getProperty(MongoConfigKey.DB_MONGO_CLIENT_PATH).let { Paths.get(it) },
                    props.getProperty(MongoConfigKey.DB_MONGO_PRINT_OUTPUT).toBoolean(),
                    props.getProperty(MongoConfigKey.DB_MONGO_SAVE_OUTPUT).toBoolean())
        }
    }

    override fun toDriver() = MongoDriver(
            buildConnectionString(mongoUri),
            tmpFilePath,
            clientPath,
            printOutput,
            saveOutput)

    private fun buildConnectionString(mongoUri: String): String {
        val matchResult = MONGO_URI_REGEX.matchEntire(mongoUri)
                ?: throw IllegalArgumentException("MongoUri does not contains a database name")


        val (_, _, host, port, username, password, database) = matchResult.destructured
//        val connectionString = ConnectionString(mongoUri)
//        // mongoUri can come with a database but currently driver's dbName is mandatory
//        if (connectionString.database == null) {
//            throw IllegalArgumentException("MongoUri does not contains a database name")
//        }
//        // mongoUri can come with a collection. It has no sense in DataMaintain's logic
//        if (connectionString.collection != null) {
//            throw IllegalArgumentException("MongoUri contains a collection name, please remove it")
//        }
        return mongoUri
    }

    override fun log() {
        logger.info { "Mongo driver configuration: " }
        logger.info { "- mongo uri -> $mongoUri" }
        logger.info { "- mongo tmp file -> $tmpFilePath" }
        logger.info { "- mongo client -> $clientPath" }
        logger.info { "- mongo print output -> $printOutput" }
        logger.info { "- mongo save output -> $saveOutput" }
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
    DB_MONGO_PRINT_OUTPUT("db.mongo.print.output", "false"),
    DB_MONGO_SAVE_OUTPUT("db.mongo.save.output", "false"),
}
