package datamaintain

class Core {

    fun run(config: Config): ExecutionReport =
            Scanner(config).scan()
                    .let { scripts -> Sorter(config).sort(scripts) }
                    .let { scripts -> Pruner(config).prune(scripts) }
                    .let { scripts -> Executor(config).execute(scripts) }

}

