package datamaintain.db.driver.jdbc

import datamaintain.core.config.ConfigKey
import datamaintain.core.config.getProperty
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.db.driver.DriverConfigKey
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class JdbcDriverConfig @JvmOverloads constructor(
        val trustUri: Boolean,
        val jdbcUri: String,
        val clientPath: Path = Paths.get(JdbcConfigKey.DB_JDBC_CLIENT_PATH.default!!),
        val printOutput: Boolean = JdbcConfigKey.DB_JDBC_PRINT_OUTPUT.default!!.toBoolean(),
        val saveOutput: Boolean = JdbcConfigKey.DB_JDBC_SAVE_OUTPUT.default!!.toBoolean()
) : DatamaintainDriverConfig(trustUri, jdbcUri, JdbcConnectionStringBuilder()) {
    companion object {
        @JvmStatic
        fun buildConfig(props: Properties): JdbcDriverConfig {
            ConfigKey.overrideBySystemProperties(props, JdbcConfigKey.values().asList())
            return JdbcDriverConfig(
                    props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean(),
                    props.getProperty(JdbcConfigKey.DB_JDBC_URI),
                    props.getProperty(JdbcConfigKey.DB_JDBC_CLIENT_PATH).let { Paths.get(it) },
                    props.getProperty(JdbcConfigKey.DB_JDBC_PRINT_OUTPUT).toBoolean(),
                    props.getProperty(JdbcConfigKey.DB_JDBC_SAVE_OUTPUT).toBoolean())
        }
    }

    override fun toDriver(connectionString: String) = JdbcDriver(
            connectionString,
            clientPath,
            printOutput,
            saveOutput)

    override fun log() {
        logger.info { "JDBC driver configuration: " }
        logger.info { "- jdbc uri -> $jdbcUri" }
        logger.info { "- jdbc client -> $clientPath" }
        logger.info { "- jdbc print output -> $printOutput" }
        logger.info { "- jdbc save output -> $saveOutput" }
        logger.info { "" }
    }
}

enum class JdbcConfigKey(
        override val key: String,
        override val default: String? = null
) : ConfigKey {
    DB_JDBC_URI("db.jdbc.uri"),
    DB_JDBC_CLIENT_PATH("db.jdbc.client.path"),
    DB_JDBC_PRINT_OUTPUT("db.jdbc.print.output", "false"),
    DB_JDBC_SAVE_OUTPUT("db.jdbc.save.output", "false"),
}
