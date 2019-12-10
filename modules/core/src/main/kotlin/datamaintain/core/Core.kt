package datamaintain.core

import datamaintain.core.config.Config
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.report.ExecutionReport
import datamaintain.core.script.Script
import datamaintain.core.step.Executor
import datamaintain.core.step.Filter
import datamaintain.core.step.Pruner
import datamaintain.core.step.Scanner
import datamaintain.core.step.sort.ByLengthAndCaseInsensitiveAlphabeticalSorter
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun runDatamaintain(config: Config, driverConfig: DatamaintainDriverConfig): ExecutionReport {
    return Core(config, driverConfig).run()
}

class Core(config: Config, driverConfig: DatamaintainDriverConfig) {

    init {
        logger.info { "config loaded: $config" }
        logger.info { "mongo driver config loaded: $driverConfig" }
    }

    val context = Context(config, driverConfig.toDriver())

    fun run(): ExecutionReport =
            Scanner(context).scan()
                    .let { scripts -> Filter(context).filter(scripts) }
                    .let { scripts ->
                        ByLengthAndCaseInsensitiveAlphabeticalSorter(context.config)
                                .sort(scripts, Script::identifier)
                    }
                    .let { scripts -> Pruner(context).prune(scripts) }
                    .let { scripts -> Executor(context).execute(scripts) }

}

