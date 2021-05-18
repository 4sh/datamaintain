package datamaintain.db.driver.jdbc

import datamaintain.core.config.ConfigKey
import datamaintain.core.config.getProperty
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.db.driver.DriverConfigKey
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

data class JdbcDriverConfig @JvmOverloads constructor(
    override val uri: String,
    override val trustUri: Boolean,
    override val printOutput: Boolean = DriverConfigKey.DB_PRINT_OUTPUT.default!!.toBoolean(),
    override val saveOutput: Boolean = DriverConfigKey.DB_SAVE_OUTPUT.default!!.toBoolean()
) : DatamaintainDriverConfig(uri, trustUri, printOutput, saveOutput, JdbcConnectionStringBuilder()) {
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

    override fun toDriver(connectionString: String) = JdbcDriver(connectionString)

    override fun log() {
        logger.info { "JDBC driver configuration: " }
        logger.info { "- jdbc uri -> $uri" }
        logger.info { "- trust uri -> $trustUri" }
        logger.info { "- print output -> $printOutput" }
        logger.info { "- save output -> $saveOutput" }
        logger.info { "" }
    }
}