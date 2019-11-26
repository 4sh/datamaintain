package datamaintain

import datamaintain.report.ExecutionReport
import datamaintain.sort.CaseInsensitiveAlphabeticalSorter

class Core {

    fun run(config: Config): ExecutionReport =
            Scanner(config).scan()
                    .let { scripts -> CaseInsensitiveAlphabeticalSorter(config).sort(scripts, Script::identifier) }
                    .let { scripts -> Pruner(config).prune(scripts) }
                    .let { scripts -> Executor(config).execute(scripts) }

}

