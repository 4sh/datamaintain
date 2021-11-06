package datamaintain.test

import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ReportExecutedScript

fun buildReportExecutedScript(scriptName: String, porcelainName: String?) =
    ReportExecutedScript(
        name = scriptName,
        porcelainName = porcelainName,
        checksum = "",
        identifier = "",
        executionStatus = ExecutionStatus.OK
    )