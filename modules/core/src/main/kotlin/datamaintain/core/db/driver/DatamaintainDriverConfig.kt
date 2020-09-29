package datamaintain.core.db.driver

interface DatamaintainDriverConfig {

    /**
     * Builds a driver based on the configuration
     */
    fun toDriver(): DatamaintainDriver

    /**
     * Logs with an info level the driver configuration
     */
    fun log()
}
