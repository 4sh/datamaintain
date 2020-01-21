package datamaintain.core.step.executor

import datamaintain.core.Context
import datamaintain.core.report.ExecutionLineReport
import datamaintain.core.report.ExecutionReport
import datamaintain.core.report.ExecutionStatus
import datamaintain.core.script.ScriptWithContent
import java.time.Instant

class Executor(private val context: Context) {
    fun execute(scripts: List<ScriptWithContent>): ExecutionReport {
        val reportLines: List<ExecutionLineReport>

        when (context.config.executionMode) {
            ExecutionMode.NORMAL -> reportLines = scripts.map { context.dbDriver.executeScript(it) }
            ExecutionMode.DRY -> reportLines = scripts.map {
                ExecutionLineReport(Instant.now(), "", ExecutionStatus.OK, it)
            }
        }

        return ExecutionReport(reportLines, Instant.now())
    }
}
