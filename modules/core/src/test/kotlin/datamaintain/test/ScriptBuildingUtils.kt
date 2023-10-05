package datamaintain.test

import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ReportExecutedScript

fun buildReportExecutedScript(scriptName: String) =
    ReportExecutedScript(
        name = scriptName,
        checksum = "",
        identifier = "",
        executionStatus = ExecutionStatus.OK
    )
