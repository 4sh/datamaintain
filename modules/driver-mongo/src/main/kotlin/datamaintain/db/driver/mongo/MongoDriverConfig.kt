package datamaintain.db.driver.mongo

import datamaintain.core.config.*
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
                                                       val mongoShell: MongoShell = DEFAULT_MONGO_SHELL,
                                                       var clientPath: Path? = null
) : DatamaintainDriverConfig(uri, trustUri, printOutput, saveOutput, MongoConnectionStringBuilder()) {
    init {
        if (clientPath == null) {
            clientPath = Paths.get(mongoShell.defaultBinaryName())
        }
    }

    companion object {
        val DEFAULT_MONGO_SHELL = MongoShell.MONGO

        @JvmStatic
        fun buildConfig(props: Properties): MongoDriverConfig {
            ConfigKey.overrideBySystemProperties(props, MongoConfigKey.values().asList())
            ConfigKey.overrideBySystemProperties(props, DriverConfigKey.values().asList())

            val mongoShell =
                MongoShell.fromNullable(props.getNullableProperty(MongoConfigKey.DB_MONGO_SHELL), DEFAULT_MONGO_SHELL)

            // default mongo path is mongo or mongosh (depends of mongo shell variable)
            val mongoPath = props.getProperty(MongoConfigKey.DB_MONGO_CLIENT_PATH, mongoShell.defaultBinaryName()).let { Paths.get(it) }

            return MongoDriverConfig(
                    uri = props.getProperty(DriverConfigKey.DB_URI),
                    printOutput = props.getProperty(DriverConfigKey.DB_PRINT_OUTPUT).toBoolean(),
                    saveOutput = props.getProperty(DriverConfigKey.DB_SAVE_OUTPUT).toBoolean(),
                    trustUri = props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean(),
                    tmpFilePath = props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    clientPath = mongoPath,
                    mongoShell = mongoShell
            )
        }
    }

    override fun toDriver(connectionString: String) = MongoDriver(
            connectionString,
            tmpFilePath,
            clientPath!!,
            printOutput,
            saveOutput,
            mongoShell)

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
    DB_MONGO_CLIENT_PATH("db.mongo.client.path"),
    DB_MONGO_SHELL("db.mongo.client.shell", MongoDriverConfig.DEFAULT_MONGO_SHELL.name),
}
