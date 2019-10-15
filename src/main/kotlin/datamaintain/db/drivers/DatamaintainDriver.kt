package datamaintain.db.drivers

import datamaintain.Script
import datamaintain.ScriptWithContent

import datamaintain.report.ExecutionStatus
import datamaintain.report.LineReport
import datamaintain.report.LineReportLevel
import java.time.Instant

interface DatamaintainDriver {

    fun executeScript(script: ScriptWithContent): ScriptExecutionReport

    fun listExecutedScripts(): Sequence<Script>

    fun markAsExecuted(script: Script)
}

class ScriptExecutionReport(override val date: Instant,
                            override val message: String,
                            val executionStatus: ExecutionStatus) : LineReport {
    override val level: LineReportLevel
        get() = if (executionStatus == ExecutionStatus.OK) LineReportLevel.INFO else LineReportLevel.ERROR
}
