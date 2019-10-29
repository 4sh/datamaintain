package datamaintain.report

import java.time.Instant

class ScriptExecutionReport(override val date: Instant,
                            override val message: String,
                            val executionStatus: ExecutionStatus) : LineReport {
    override val level: LineReportLevel
        get() = if (executionStatus == ExecutionStatus.OK) LineReportLevel.INFO else LineReportLevel.ERROR
}