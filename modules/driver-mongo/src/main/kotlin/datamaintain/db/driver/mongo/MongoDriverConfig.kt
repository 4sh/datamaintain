package datamaintain.db.driver.mongo

import datamaintain.core.config.*
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.db.driver.DriverConfigKey
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class MongoDriverConfig @JvmOverloads constructor(val mongoUri: String,
                                                       val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                                                       var clientPath: Path? = null,
                                                       val printOutput: Boolean = MongoConfigKey.DB_MONGO_PRINT_OUTPUT.default!!.toBoolean(),
                                                       val saveOutput: Boolean = MongoConfigKey.DB_MONGO_SAVE_OUTPUT.default!!.toBoolean(),
                                                       val mongoShell: MongoShell = DEFAULT_MONGO_SHELL,
                                                       val trustUri: Boolean
) : DatamaintainDriverConfig(trustUri, mongoUri, MongoConnectionStringBuilder()) {
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
                    props.getProperty(MongoConfigKey.DB_MONGO_URI),
                    props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    mongoPath,
                    props.getProperty(MongoConfigKey.DB_MONGO_PRINT_OUTPUT).toBoolean(),
                    props.getProperty(MongoConfigKey.DB_MONGO_SAVE_OUTPUT).toBoolean(),
                    mongoShell,
                    props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean()
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
    DB_MONGO_CLIENT_PATH("db.mongo.client.path"),
    DB_MONGO_SHELL("db.mongo.client.shell", MongoDriverConfig.DEFAULT_MONGO_SHELL.name),
    DB_MONGO_PRINT_OUTPUT("db.mongo.print.output", "false"),
    DB_MONGO_SAVE_OUTPUT("db.mongo.save.output", "false"),
}
