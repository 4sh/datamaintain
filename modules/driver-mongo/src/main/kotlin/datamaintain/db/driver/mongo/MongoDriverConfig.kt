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

data class MongoDriverConfig @JvmOverloads constructor(override val uri: String,
                                                       override val printOutput: Boolean = DriverConfigKey.DB_PRINT_OUTPUT.default!!.toBoolean(),
                                                       override val saveOutput: Boolean = DriverConfigKey.DB_SAVE_OUTPUT.default!!.toBoolean(),
                                                       override val trustUri: Boolean,
                                                       val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                                                       val clientPath: Path = Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
) : DatamaintainDriverConfig(uri, trustUri, printOutput, saveOutput, MongoConnectionStringBuilder()) {
    companion object {
        @JvmStatic
        fun buildConfig(props: Properties): MongoDriverConfig {
            ConfigKey.overrideBySystemProperties(props, MongoConfigKey.values().asList())
            ConfigKey.overrideBySystemProperties(props, DriverConfigKey.values().asList())
            return MongoDriverConfig(
                    props.getProperty(DriverConfigKey.DB_URI),
                    props.getProperty(DriverConfigKey.DB_PRINT_OUTPUT).toBoolean(),
                    props.getProperty(DriverConfigKey.DB_SAVE_OUTPUT).toBoolean(),
                    props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean(),
                    props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    props.getProperty(MongoConfigKey.DB_MONGO_CLIENT_PATH).let { Paths.get(it) }
            )
        }
    }

    override fun toDriver(connectionString: String) = MongoDriver(
            connectionString,
            tmpFilePath,
            clientPath,
            printOutput,
            saveOutput)

    override fun log() {
        logger.info { "Mongo driver configuration: " }
        logger.info { "- mongo uri -> $uri" }
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
    DB_MONGO_TMP_PATH("db.mongo.tmp.path", "/tmp/datamaintain.tmp"),
    DB_MONGO_CLIENT_PATH("db.mongo.client.path", "mongo"),
}
