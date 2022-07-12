package datamaintain.test

import datamaintain.domain.script.ExecutionStatus
import datamaintain.domain.script.ReportExecutedScript

fun buildReportExecutedScript(scriptName: String, porcelainName: String?) =
    ReportExecutedScript(
        name = scriptName,
        porcelainName = porcelainName,
        checksum = "",
        identifier = "",
        executionStatus = ExecutionStatus.OK
    )