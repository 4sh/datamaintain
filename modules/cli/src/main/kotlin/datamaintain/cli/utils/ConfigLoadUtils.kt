package datamaintain.cli.utils

import datamaintain.cli.DbType
import datamaintain.cli.DbTypeNotFoundException
import datamaintain.core.config.DatamaintainConfig
import datamaintain.db.driver.mongo.MongoDriverConfig
import java.util.*

private fun loadDriverConfig(props: Properties): MongoDriverConfig {
    return when (props.getProperty("dbType")) {
        DbType.MONGO.value -> MongoDriverConfig.buildConfig(props)
        else -> throw DbTypeNotFoundException(props.getProperty("dbType"))
    }
}

fun loadConfig(props: Properties): DatamaintainConfig {
    val driverConfig = loadDriverConfig(props)
    return DatamaintainConfig.buildConfig(driverConfig, props)
}