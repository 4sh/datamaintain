package datamaintain.core.db.driver

import datamaintain.core.config.ConfigKey

abstract class DatamaintainDriverConfig(val dbType: String,
                                        open val uri: String,
                                        open val trustUri: Boolean,
                                        open val printOutput: Boolean = DriverConfigKey.DB_PRINT_OUTPUT.default!!.toBoolean(),
                                        open val saveOutput: Boolean = DriverConfigKey.DB_SAVE_OUTPUT.default!!.toBoolean(),
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
    DB_URI("db.uri"),
    DB_TRUST_URI("db.trust.uri", "false"),
    DB_PRINT_OUTPUT("db.print.output", "false"),
    DB_SAVE_OUTPUT("db.save.output", "false")
}
