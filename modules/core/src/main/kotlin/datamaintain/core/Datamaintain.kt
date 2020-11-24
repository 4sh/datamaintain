package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.report.Report
import datamaintain.core.step.Filter
import datamaintain.core.step.Pruner
import datamaintain.core.step.Scanner
import datamaintain.core.step.check.Checker
import datamaintain.core.step.check.CheckerData
import datamaintain.core.step.executor.Executor
import datamaintain.core.step.sort.Sorter
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Datamaintain(config: DatamaintainConfig) {

    init {
        if (config.verbose) {
            config.log()
            config.driverConfig.log()
        }
    }

    val context = Context(
            config,
            config.driverConfig.toDriver()
    )

    fun updateDatabase(): Report {
        return Scanner(context).scan()
                .let { scannedScripts ->
                    val checkerData = CheckerData(scannedScripts = scannedScripts.asSequence())

                    checkerData.filteredScripts = Filter(context).filter(scannedScripts).asSequence()
                    checkerData
                }
                .let { checkerData ->
                    checkerData.sortedScripts = Sorter(context).sort(checkerData.filteredScripts.toList()).asSequence()
                    checkerData
                }
                .let { checkerData ->
                    checkerData.prunedScripts = Pruner(context).prune(checkerData.sortedScripts.toList()).asSequence()
                    checkerData
                }
                .let { checkerData -> Checker(context).check(checkerData) }
                .let { scripts -> Executor(context).execute(scripts) }
    }

    fun listExecutedScripts() = context.dbDriver.listExecutedScripts()
}
