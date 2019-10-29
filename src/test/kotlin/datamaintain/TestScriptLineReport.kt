package datamaintain

import datamaintain.report.ExecutionStatus
import datamaintain.report.ScriptLineReport
import java.time.Instant

class TestScriptLineReport(
        override val date: Instant,
        override val message: String,
        override val executionStatus: ExecutionStatus
) : ScriptLineReport