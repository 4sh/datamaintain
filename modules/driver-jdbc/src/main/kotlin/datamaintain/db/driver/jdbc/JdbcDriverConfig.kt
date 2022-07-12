package datamaintain.db.driver.jdbc

import datamaintain.core.config.ConfigKey
import datamaintain.core.config.getProperty
import datamaintain.core.db.driver.DBType
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.db.driver.DriverConfigKey
import datamaintain.core.exception.DatamaintainBuilderMandatoryException
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

data class JdbcDriverConfig @JvmOverloads constructor(
    override val uri: String,
    override val trustUri: Boolean,
    override val printOutput: Boolean = DriverConfigKey.DB_PRINT_OUTPUT.default!!.toBoolean(),
    override val saveOutput: Boolean = DriverConfigKey.DB_SAVE_OUTPUT.default!!.toBoolean()
) : DatamaintainDriverConfig(DBType.JDBC.toString(), uri, trustUri, printOutput, saveOutput, JdbcConnectionStringBuilder()) {
    companion object {
        @JvmStatic
        fun buildConfig(props: Properties): JdbcDriverConfig {
            ConfigKey.overrideBySystemProperties(props, DriverConfigKey.values().asList())
            return JdbcDriverConfig(
                props.getProperty(DriverConfigKey.DB_URI),
                props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean(),
                props.getProperty(DriverConfigKey.DB_SAVE_OUTPUT).toBoolean(),
                props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean()
            )
        }
    }

    constructor(builder: Builder): this(
        builder.uri,
        builder.trustUri,
        builder.printOutput,
        builder.saveOutput
    )

    override fun toDriver(connectionString: String) = JdbcDriver(connectionString)

    override fun log() {
        logger.info { "JDBC driver configuration: " }
        logger.info { "- jdbc uri -> $uri" }
        logger.info { "- trust uri -> $trustUri" }
        logger.info { "- print output -> $printOutput" }
        logger.info { "- save output -> $saveOutput" }
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

        fun withUri(uri: String) = apply { this.uri = uri }
        fun withPrintOutput(printOutput: Boolean) = apply { this.printOutput = printOutput }
        fun withSaveOutput(saveOutput: Boolean) = apply { this.saveOutput = saveOutput }
        fun withTrustUri(trustUri: Boolean) = apply { this.trustUri = trustUri }

        fun build(): JdbcDriverConfig {
            if (!::uri.isInitialized) {
                throw DatamaintainBuilderMandatoryException("JdbcDriverConfigBuilder", "uri")
            }

            return JdbcDriverConfig(this)
        }
    }
}
