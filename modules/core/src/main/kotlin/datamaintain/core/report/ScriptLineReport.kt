package datamaintain.core.report

import datamaintain.core.script.ExecutionStatus

interface ScriptLineReport: LineReport {
    val executionStatus: ExecutionStatus

    override val level: LineReportLevel
        get() = if (executionStatus == ExecutionStatus.OK) LineReportLevel.INFO else LineReportLevel.ERROR
}