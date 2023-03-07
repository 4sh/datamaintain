package datamaintain.test

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import java.nio.file.Paths

fun buildTestContext(dbDriver: DatamaintainDriver) =  Context(
    DatamaintainConfig(
        path = Paths.get(""),
        identifierRegex = Regex(""),
        driverConfig = FakeDriverConfig()
    ),
    dbDriver = dbDriver
)