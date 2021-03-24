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
        val jdbcUri: String
) : DatamaintainDriverConfig(trustUri, jdbcUri, JdbcConnectionStringBuilder()) {
    companion object {
        @JvmStatic
        fun buildConfig(props: Properties): JdbcDriverConfig {
            ConfigKey.overrideBySystemProperties(props, JdbcConfigKey.values().asList())
            return JdbcDriverConfig(
                    props.getProperty(DriverConfigKey.DB_TRUST_URI).toBoolean(),
                    props.getProperty(JdbcConfigKey.DB_JDBC_URI))
        }
    }

    override fun toDriver(connectionString: String) = JdbcDriver(connectionString)

    override fun log() {
        logger.info { "JDBC driver configuration: " }
        logger.info { "- jdbc uri -> $jdbcUri" }
        logger.info { "- trust uri -> $trustUri" }
        logger.info { "" }
    }
}

enum class JdbcConfigKey(
        override val key: String,
        override val default: String? = null
) : ConfigKey {
    DB_JDBC_URI("db.jdbc.uri")
}
