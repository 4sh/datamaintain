package datamaintain.core

import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.report.ExecutionReport
import datamaintain.core.script.Script
import datamaintain.core.step.Executor
import datamaintain.core.step.Filter
import datamaintain.core.step.Pruner
import datamaintain.core.step.Scanner
import datamaintain.core.step.sort.ByLengthAndCaseInsensitiveAlphabeticalSorter

fun runDatamaintain(config: Config, driver: DatamaintainDriver) =
        Core().run(Context(config, driver))

class Core {

    fun run(context: Context): ExecutionReport =
            Scanner(context).scan()
                    .let { scripts -> Filter(context).filter(scripts) }
                    .let { scripts ->
                        ByLengthAndCaseInsensitiveAlphabeticalSorter(context.config)
                                .sort(scripts, Script::identifier)
                    }
                    .let { scripts -> Pruner(context).prune(scripts) }
                    .let { scripts -> Executor(context).execute(scripts) }

}

