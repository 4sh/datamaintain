package datamaintain.core.report

interface ScriptLineReport: LineReport {
    val executionStatus: ExecutionStatus

    override val level: LineReportLevel
        get() = if (executionStatus == ExecutionStatus.OK) LineReportLevel.INFO else LineReportLevel.ERROR
}