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
import mu.KotlinLogging
import java.time.Clock
import java.time.ZoneId

private val logger = KotlinLogging.logger {}

class Datamaintain(config: DatamaintainConfig, clock: Clock = Clock.system(ZoneId.systemDefault())) {
//    private val reportSender: IExecutionWorkflowMessagesSender? = config.monitoringConfiguration?.let { Http4KExecutionWorkflowMessagesSender(
//        baseUrl = it.apiUrl,
//        clock = clock
//    ) }

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
        val checkerData = CheckerData()

        return Scanner(context).scan()
                .let { scannedScripts ->
                    checkerData.scannedScripts = scannedScripts.asSequence()
                    scannedScripts
                }
                .let { scannedScripts ->
                    val filteredScripts = Filter(context).filter(scannedScripts)
                    checkerData.filteredScripts = filteredScripts.asSequence()
                    filteredScripts
                }
                .let { filteredScripts ->
                    val sortedScripts = Sorter(context).sort(filteredScripts)
                    checkerData.sortedScripts = sortedScripts.asSequence()
                    sortedScripts
                }
                .let { sortedScripts ->
                    val prunedScripts = Pruner(context).prune(sortedScripts)
                    checkerData.prunedScripts = prunedScripts.asSequence()
                }
                .let { Checker(context).check(checkerData) }
                .let { scripts -> Executor(context).execute(scripts) }
    }

    fun listExecutedScripts() = context.dbDriver.listExecutedScripts()
}
