package datamaintain.core

import datamaintain.core.report.ExecutionReport
import datamaintain.core.script.Script
import datamaintain.core.step.Executor
import datamaintain.core.step.Filter
import datamaintain.core.step.Pruner
import datamaintain.core.step.Scanner
import datamaintain.sort.ByLengthAndCaseInsensitiveAlphabeticalSorter

class Core {

    fun run(config: Config): ExecutionReport =
            Scanner(config).scan()
                    .let { scripts -> Filter(config).filter(scripts) }
                    .let { scripts -> ByLengthAndCaseInsensitiveAlphabeticalSorter(config).sort(scripts, Script::identifier) }
                    .let { scripts -> Pruner(config).prune(scripts) }
                    .let { scripts -> Executor(config).execute(scripts) }

}

