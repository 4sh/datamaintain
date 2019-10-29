package datamaintain

import datamaintain.report.ExecutionLineReport
import datamaintain.report.ExecutionReport
import java.time.Instant

class Executor(val config: Config) {
    fun execute(scripts: List<ScriptWithContent>): ExecutionReport {
        val reportLines = scripts.map { it to config.dbDriver.executeScript(it) }
                .map { (script, scriptExecutionReport) ->
                    ExecutionLineReport(
                            scriptExecutionReport.date,
                            scriptExecutionReport.message,
                            scriptExecutionReport.executionStatus,
                            script
                    )
                }

        return ExecutionReport(reportLines, Instant.now())
    }
}