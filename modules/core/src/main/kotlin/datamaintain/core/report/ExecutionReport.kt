package datamaintain.core.report

import java.time.Instant

class ExecutionReport(override val lines: List<ExecutionLineReport>, override val date: Instant) : Report {
    override val status: ReportStatus
        get() = if (lines.all { executionLineReport -> executionLineReport.executionStatus.correctlyExecuted() }) {
            ReportStatus.OK
        } else {
            ReportStatus.KO
        }
}