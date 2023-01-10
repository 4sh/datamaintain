package datamaintain.db.driver.mongo

import datamaintain.core.config.*
import datamaintain.core.db.driver.DBType
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.db.driver.DriverConfigKey
import datamaintain.core.exception.DatamaintainBuilderMandatoryException
import datamaintain.db.driver.mongo.exception.DatamaintainMongoClientNotFound
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class MongoDriverConfig @JvmOverloads constructor(override val uri: String,
                                                       override val executedScriptsStorageName: String = DriverConfigKey.EXECUTED_SCRIPTS_STORAGE_NAME.default!!,
                                                       override val printOutput: Boolean = DriverConfigKey.DB_PRINT_OUTPUT.default!!.toBoolean(),
                                                       override val saveOutput: Boolean = DriverConfigKey.DB_SAVE_OUTPUT.default!!.toBoolean(),
                                                       override val trustUri: Boolean,
                                                       val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
                                                       val mongoShell: MongoShell = DEFAULT_MONGO_SHELL,
                                                       var clientExecutable: String = mongoShell.defaultBinaryName(),
) : DatamaintainDriverConfig(
    dbType = DBType.MONGO.toString(),
    uri = uri,
    trustUri = trustUri,
    printOutput = printOutput,
    saveOutput = saveOutput,
    connectionStringBuilder = MongoConnectionStringBuilder(),
    executedScriptsStorageName = executedScriptsStorageName
) {

    constructor(builder: Builder): this(
        uri = builder.uri,
        executedScriptsStorageName = builder.executedScriptsCollectionName,
        printOutput = builder.printOutput,
        saveOutput = builder.saveOutput,
        trustUri = builder.trustUri,
        tmpFilePath = builder.tmpFilePath,
        mongoShell = builder.mongoShell,
        clientExecutable = builder.clientExecutable?: builder.mongoShell.defaultBinaryName(),
    )

    companion object {
        val DEFAULT_MONGO_SHELL = MongoShell.MONGO

        @JvmStatic
        fun buildConfig(props: Properties): MongoDriverConfig {
            ConfigKey.overrideBySystemProperties(props, MongoConfigKey.values().asList())
            ConfigKey.overrideBySystemProperties(props, DriverConfigKey.values().asList())

            val mongoShell =
                MongoShell.fromNullable(props.getNullableProperty(MongoConfigKey.DB_MONGO_SHELL), DEFAULT_MONGO_SHELL)

            // default mongo executable is 'mongo' or 'mongosh' command
            val mongoPath = props.getProperty(MongoConfigKey.DB_MONGO_CLIENT_PATH, mongoShell.defaultBinaryName())

            return MongoDriverConfig(
                    uri = props.getProperty(DriverConfigKey.DB_URI),
                    printOutput = props.getProperty(DriverConfigKey.DB_PRINT_OUTPUT).toBoolean(),
                    saveOutput = props.getProperty(DriverConfigKey.DB_SAVE_OUTPUT).toBoolean(),
                    trustUri = props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean(),
                    tmpFilePath = props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) },
                    clientExecutable = mongoPath,
                    mongoShell = mongoShell,
                    executedScriptsStorageName = props.getProperty(DriverConfigKey.EXECUTED_SCRIPTS_STORAGE_NAME)
            )
        }
    }

    override fun toDriver(connectionString: String): MongoDriver {
        ensureMongoExecutableIsPresent()

        return MongoDriver(
            mongoUri = connectionString,
            executedScriptsCollectionName = executedScriptsStorageName,
            tmpFilePath = tmpFilePath,
            clientExecutable = clientExecutable,
            printOutput = printOutput,
            saveOutput = saveOutput,
            mongoShell = mongoShell
        )
    }

    /**
     * The clientExecutable designed the mongo executable, it can be a :
     * * command like 'mongo'
     * * path like '/path/to/mongo'
     *
     * If it is a command then ensure the program exists in $PATH.
     *
     * If it is a path then ensure the path exists, note that 'mongo' can be resolved has
     * './mongo' if mongo is not in $PATH
     */
    fun ensureMongoExecutableIsPresent() {
        val clientExecutablePath = Paths.get(clientExecutable)

        // If a filename is passed, it can be either a command or a filename
        val canBeACommand = clientExecutablePath.fileName.toString() == clientExecutable

        if (canBeACommand) {
            val pathEnv = System.getenv("PATH").split(":")
            val mongoClientsFound = pathEnv.asSequence()
                    .map { Paths.get(it).resolve(clientExecutable) }
                    .filter { Files.exists(it) }
                    .toList()

            if (mongoClientsFound.isNotEmpty()) {
                return
            }
        }

        val exists = Files.exists(clientExecutablePath)
        if (exists) {
            if (canBeACommand) {
                this.clientExecutable = clientExecutablePath.toAbsolutePath().toString()
            }
        } else {
            throw DatamaintainMongoClientNotFound(clientExecutable)
        }
    }

    override fun log() {
        logger.info { "Mongo driver configuration: " }
        logger.info { "- mongo uri -> $uri" }
        logger.info { "- mongo tmp file -> $tmpFilePath" }
        logger.info { "- mongo client -> $clientExecutable" }
        logger.info { "- mongo print output -> $printOutput" }
        logger.info { "- mongo save output -> $saveOutput" }
        logger.info { "- mongo executed scripts collection name -> $executedScriptsStorageName" }
        logger.info { "" }
    }

    class Builder {
        // mandatory
        lateinit var uri: String
            private set

        // optional
        var printOutput: Boolean = DriverConfigKey.DB_PRINT_OUTPUT.default!!.toBoolean()
            private set
        var saveOutput: Boolean = DriverConfigKey.DB_SAVE_OUTPUT.default!!.toBoolean()
            private set
        var trustUri: Boolean = DriverConfigKey.DB_TRUST_URI.default!!.toBoolean()
            private set
        var tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!)
            private set
        var mongoShell: MongoShell = DEFAULT_MONGO_SHELL
            private set
        var clientExecutable: String? = null
            private set
        var executedScriptsCollectionName: String = DriverConfigKey.EXECUTED_SCRIPTS_STORAGE_NAME.default!!
            private set

        fun withUri(uri: String) = apply { this.uri = uri }
        fun withPrintOutput(printOutput: Boolean) = apply { this.printOutput = printOutput }
        fun withSaveOutput(saveOutput: Boolean) = apply { this.saveOutput = saveOutput }
        fun withTrustUri(trustUri: Boolean) = apply { this.trustUri = trustUri }
        fun withTmpFilePath(tmpFilePath: Path) = apply { this.tmpFilePath = tmpFilePath }
        fun withMongoShell(mongoShell: MongoShell) = apply { this.mongoShell = mongoShell }
        fun withClientExecutable(clientExecutable: String?) = apply { this.clientExecutable = clientExecutable }
        fun withExecutedScriptsCollectionName(executedScriptsCollectionName: String) = apply { this.executedScriptsCollectionName = executedScriptsCollectionName }

        fun build(): MongoDriverConfig {
            if (!::uri.isInitialized) {
                throw DatamaintainBuilderMandatoryException("MongoDriverConfigBuilder", "uri")
            }

            return MongoDriverConfig(this)
        }
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
