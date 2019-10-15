package datamaintain.report

import java.time.Instant

class ExecutionReport(override val lines: List<ExecutionLineReport>, override val date: Instant) : Report {
    override val status: ReportStatus
        get() = if (lines.all { executionLineReport -> executionLineReport.executionStatus == ExecutionStatus.OK })
                    ReportStatus.OK
                else
                    ReportStatus.KO
}