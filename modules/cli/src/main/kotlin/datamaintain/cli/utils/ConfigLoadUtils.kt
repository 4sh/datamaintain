package datamaintain.cli.utils

import datamaintain.cli.DbType
import datamaintain.cli.DbTypeNotFoundException
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.db.driver.jdbc.JdbcDriverConfig
import datamaintain.db.driver.mongo.MongoDriverConfig
import java.util.*

private fun loadDriverConfig(props: Properties): DatamaintainDriverConfig {
    return when (props.getProperty("dbType")) {
        DbType.MONGO.value -> MongoDriverConfig.buildConfig(props)
        DbType.JDBC.value -> JdbcDriverConfig.buildConfig(props)
        else -> throw DbTypeNotFoundException(props.getProperty("dbType"))
    }
}

fun loadConfig(props: Properties): DatamaintainConfig {
    val driverConfig = loadDriverConfig(props)
    return DatamaintainConfig.buildConfig(driverConfig, props)
}