package datamaintain.core.db.driver

import datamaintain.core.config.ConfigKey

abstract class DatamaintainDriverConfig(trustUri: Boolean) {

    /**
     * Builds a driver based on the configuration
     */
    abstract fun toDriver(): DatamaintainDriver

    /**
     * Logs with an info level the driver configuration
     */
    abstract fun log()
}

enum class DriverConfigKey(override val key: String,
                           override val default: String? = null): ConfigKey {
    DB_TRUST_URI("db.trust.uri", "false")
}