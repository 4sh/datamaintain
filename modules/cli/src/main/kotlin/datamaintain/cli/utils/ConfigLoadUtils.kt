package datamaintain.cli.utils

import datamaintain.cli.DbType
import datamaintain.cli.DbTypeNotFoundException
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.db.driver.jdbc.JdbcDriverConfig
import datamaintain.db.driver.mongo.MongoDriverConfig
import java.util.*

private fun loadDriverConfig(props: Properties): DatamaintainDriverConfig {
    var dbType: String? = null

    if (props.containsKey("db.type")) {
        dbType = props.getProperty("db.type")
    } else if (props.containsKey("dbType")) { // Old key to set database type. Keep it to avoid breaking changes
        dbType = props.getProperty("dbType")
    }

    return when (dbType) {
        DbType.MONGO.value -> MongoDriverConfig.buildConfig(props)
        DbType.JDBC.value -> JdbcDriverConfig.buildConfig(props)
        else -> throw DbTypeNotFoundException(props.getProperty("db.type"))
    }
}

fun loadConfig(props: Properties): DatamaintainConfig {
    val driverConfig = loadDriverConfig(props)
    return DatamaintainConfig.buildConfig(driverConfig, props)
}