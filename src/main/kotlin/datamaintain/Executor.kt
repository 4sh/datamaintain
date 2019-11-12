package datamaintain

import datamaintain.report.ExecutionReport
import java.time.Instant

class Executor(val config: Config) {
    fun execute(scripts: List<ScriptWithContent>): ExecutionReport {
        val reportLines = scripts.map { config.dbDriver.executeScript(it) }
        return ExecutionReport(reportLines, Instant.now())
    }
}