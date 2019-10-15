package datamaintain.report

import datamaintain.Script
import java.time.Instant

class ExecutionLineReport(
        override val date: Instant,
        override val message: String,
        val script: Script,
        val executionStatus: ExecutionStatus
) : LineReport {
    override val level: LineReportLevel
        get() = if (executionStatus == ExecutionStatus.OK) LineReportLevel.INFO else LineReportLevel.ERROR
}