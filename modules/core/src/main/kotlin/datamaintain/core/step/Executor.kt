package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.report.ExecutionReport
import datamaintain.core.script.ScriptWithContent
import java.time.Instant

class Executor(private val context: Context) {
    fun execute(scripts: List<ScriptWithContent>): ExecutionReport {
        val reportLines = scripts.map { context.dbDriver.executeScript(it) }
        return ExecutionReport(reportLines, Instant.now())
    }
}