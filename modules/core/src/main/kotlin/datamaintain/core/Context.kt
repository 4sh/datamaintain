package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.report.Report
import datamaintain.core.report.ReportBuilder

data class Context(
        val config: DatamaintainConfig,
        val dbDriver: DatamaintainDriver,
        val reportBuilder: ReportBuilder = ReportBuilder()
)
