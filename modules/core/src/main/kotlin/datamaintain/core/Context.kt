package datamaintain.core

import datamaintain.core.db.driver.DatamaintainDriver
import java.io.InputStream
import java.util.*

class Context(
        val config: Config,
        val dbDriver: DatamaintainDriver
) {
    companion object {
        fun loadContext(configIs: InputStream): Context {
            val props = Properties()
            props.load(configIs)

            val config = Config.buildConfig(props)
            val configuredDriver = config.dbDriverName?.let { driverLoaders[it]?.invoke(props) }

            return Context(
                    config,
                    configuredDriver
                            ?: driverLoaders.values.map { it.invoke(props) }.first()
                            ?: throw IllegalStateException("cannot load any driver")
            )
        }
    }
}

var driverLoaders: MutableMap<String, ((Properties) -> DatamaintainDriver)> = mutableMapOf()

fun loadDriver(driverName: String, driverLoader: (Properties) -> DatamaintainDriver) {
    driverLoaders[driverName] = driverLoader
}