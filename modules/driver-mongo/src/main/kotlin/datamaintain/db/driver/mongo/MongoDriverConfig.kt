package datamaintain.db.driver.mongo

import datamaintain.core.config.ConfigKey
import datamaintain.core.config.getProperty
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.db.driver.DriverConfigKey
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class MongoDriverConfig @JvmOverloads constructor(val mongoUri: String,
                                                       val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                                                       val clientPath: Path = Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!),
                                                       val printOutput: Boolean = MongoConfigKey.DB_MONGO_PRINT_OUTPUT.default!!.toBoolean(),
                                                       val saveOutput: Boolean = MongoConfigKey.DB_MONGO_SAVE_OUTPUT.default!!.toBoolean(),
                                                       val trustUri: Boolean
) : DatamaintainDriverConfig(trustUri) {
    companion object {
        @JvmStatic
        fun buildConfig(props: Properties): MongoDriverConfig {
            ConfigKey.overrideBySystemProperties(props, MongoConfigKey.values().asList())
            ConfigKey.overrideBySystemProperties(props, DriverConfigKey.values().asList())
            return MongoDriverConfig(
                    props.getProperty(MongoConfigKey.DB_MONGO_URI),
                    props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    props.getProperty(MongoConfigKey.DB_MONGO_CLIENT_PATH).let { Paths.get(it) },
                    props.getProperty(MongoConfigKey.DB_MONGO_PRINT_OUTPUT).toBoolean(),
                    props.getProperty(MongoConfigKey.DB_MONGO_SAVE_OUTPUT).toBoolean(),
                    props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean()
            )
        }
    }

    override fun toDriver() = MongoDriver(
            ConnectionString.buildConnectionString(mongoUri, trustUri),
            tmpFilePath,
            clientPath,
            printOutput,
            saveOutput)

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
