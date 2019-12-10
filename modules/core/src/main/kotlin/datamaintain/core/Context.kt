package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver

data class Context(
        val config: DatamaintainConfig,
        val dbDriver: DatamaintainDriver
)
