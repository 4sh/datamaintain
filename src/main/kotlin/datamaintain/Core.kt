package datamaintain

import datamaintain.report.ExecutionReport
import datamaintain.sort.ByLengthAndCaseInsensitiveAlphabeticalSorter

class Core {

    fun run(config: Config): ExecutionReport =
            Scanner(config).scan()
                    .let { scripts -> Filter(config).filter(scripts) }
                    .let { scripts -> ByLengthAndCaseInsensitiveAlphabeticalSorter(config).sort(scripts, Script::identifier) }
                    .let { scripts -> Pruner(config).prune(scripts) }
                    .let { scripts -> Executor(config).execute(scripts) }

}

