package datamaintain.core.report

import datamaintain.core.script.ExecutionStatus
import java.time.Instant

class ExecutionReport(override val lines: List<ExecutionLineReport>, override val date: Instant) : Report {
    override val status: ReportStatus
        get() = if (lines.all { executionLineReport ->
                    executionLineReport.executionStatus == ExecutionStatus.OK ||
                            executionLineReport.executionStatus == ExecutionStatus.FORCE_MARKED_AS_EXECUTED ||
                            executionLineReport.executionStatus == ExecutionStatus.SHOULD_BE_EXECUTED
                })
            ReportStatus.OK
        else
            ReportStatus.KO
}