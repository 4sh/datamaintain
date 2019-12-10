package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.report.ExecutionReport
import datamaintain.core.script.Script
import datamaintain.core.step.Executor
import datamaintain.core.step.Filter
import datamaintain.core.step.Pruner
import datamaintain.core.step.Scanner
import datamaintain.core.step.sort.ByLengthAndCaseInsensitiveAlphabeticalSorter
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun runDatamaintain(config: DatamaintainConfig): ExecutionReport {
    return Datamaintain(config).run()
}

class Datamaintain(config: DatamaintainConfig) {

    init {
        config.log()
        config.driverConfig.log()
    }

    val context = Context(config, config.driverConfig.toDriver())

    fun run(): ExecutionReport {
        val executionReport = Scanner(context).scan()
                .let { scripts -> Filter(context).filter(scripts) }
                .let { scripts ->
                    ByLengthAndCaseInsensitiveAlphabeticalSorter(context.config)
                            .sort(scripts, Script::identifier)
                }
                .let { scripts -> Pruner(context).prune(scripts) }
                .let { scripts -> Executor(context).execute(scripts) }

        executionReport.lines.forEach {
            logger.info { it.message }
        }

        return executionReport
    }

}

