package datamaintain.core

import datamaintain.core.config.Config
import datamaintain.core.db.driver.DatamaintainDriver

data class Context(
        val config: Config,
        val dbDriver: DatamaintainDriver
)
