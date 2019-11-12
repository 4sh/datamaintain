package datamaintain

import datamaintain.report.ExecutionReport
import datamaintain.sort.AlphabeticalSorter

class Core {

    fun run(config: Config): ExecutionReport =
            Scanner(config).scan()
                    .let { scripts -> AlphabeticalSorter(config).sort(scripts) }
                    .let { scripts -> Pruner(config).prune(scripts) }
                    .let { scripts -> Executor(config).execute(scripts) }

}

