package datamaintain.core.db.driver

import datamaintain.core.config.ConfigKey

abstract class DatamaintainDriverConfig(private val trustUri: Boolean,
                                        private val uri: String,
                                        private val connectionStringBuilder: ConnectionStringBuilder) {

    fun toDriver(): DatamaintainDriver {
        return toDriver(connectionStringBuilder.buildConnectionString(uri, trustUri))
    }

    /**
     * Builds a driver based on the configuration
     */
    protected abstract fun toDriver(connectionString: String): DatamaintainDriver

    /**
     * Logs with an info level the driver configuration
     */
    abstract fun log()
}

enum class DriverConfigKey(override val key: String,
                           override val default: String? = null): ConfigKey {
    DB_TRUST_URI("db.trust.uri", "false")
}