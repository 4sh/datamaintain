package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.step.Filter
import datamaintain.core.step.Pruner
import datamaintain.core.step.Scanner
import datamaintain.core.step.check.Checker
import datamaintain.core.step.check.CheckerData
import datamaintain.core.step.executor.Executor
import datamaintain.core.step.sort.Sorter
import datamaintain.domain.report.ExecutionId
import datamaintain.domain.report.IExecutionWorkflowMessagesSender
import datamaintain.domain.report.Report
import datamaintain.monitoring.Http4KExecutionWorkflowMessagesSender
import mu.KotlinLogging
import java.time.Clock
import java.time.ZoneId

private val logger = KotlinLogging.logger {}

class Datamaintain(config: DatamaintainConfig, clock: Clock = Clock.system(ZoneId.systemDefault())) {
    private val reportSender: IExecutionWorkflowMessagesSender? = config.monitoringConfiguration?.let { Http4KExecutionWorkflowMessagesSender(
        baseUrl = it.apiUrl,
        clock = clock
    ) }

    init {
        if (config.logs.verbose && !config.logs.porcelain) {
            config.log()
            config.scanner.log()
        }
    }

    val context = Context(
            config,
            config.driverConfig.toDriver()
    )

    fun updateDatabase(): Report {
        val executionId: ExecutionId? = reportSender?.startExecution()

        val scannedScripts = Scanner(context).scan()
        val filteredScripts = Filter(context).filter(scannedScripts)
        val sortedScripts = Sorter(context).sort(filteredScripts)
        val prunedScripts = Pruner(context).prune(sortedScripts)

        Checker(context).check(CheckerData(
            scannedScripts.asSequence(),
            filteredScripts.asSequence(),
            sortedScripts.asSequence(),
            prunedScripts.asSequence()
        ))

        val report = Executor(context, reportSender).execute(prunedScripts, executionId)

        if (executionId != null) {
            reportSender?.sendReport(executionId, report)
        }

        return report
    }

    fun listExecutedScripts() = context.dbDriver.listExecutedScripts()
}
