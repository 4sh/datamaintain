package datamaintain.core.step

import datamaintain.core.Config
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.report.ExecutionReport
import java.time.Instant

class Executor(val config: Config) {
    fun execute(scripts: List<ScriptWithContent>): ExecutionReport {
        val reportLines = scripts.map { config.dbDriver.executeScript(it) }
        return ExecutionReport(reportLines, Instant.now())
    }
}