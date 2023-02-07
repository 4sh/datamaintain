package datamaintain.cli.app.update.db

import datamaintain.cli.app.DatamaintainCliCommand
import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainException
import kotlin.system.exitProcess

fun defaultUpdateDbRunner(config: DatamaintainConfig) {
    Datamaintain(config).updateDatabase().print(config.logs.verbose, porcelain = config.logs.porcelain)
}

abstract class DatamaintainCliUpdateDbCommand(
    name: String,
    val runner: (DatamaintainConfig) -> Unit,
    help: String = ""
): DatamaintainCliCommand(name, help) {
    override fun executeCommand(config: DatamaintainConfig) {
        try {
            runner(config)
        } catch (e: DatamaintainException) {
            val verbose: Boolean = config.logs.verbose
            val porcelain: Boolean = config.logs.porcelain

            echo("Error at step ${e.step}", err = true)
            e.report.print(verbose, porcelain = porcelain)
            echo("")
            echo(e.message, err = true)

            if (e.resolutionMessage.isNotEmpty()) {
                echo(e.resolutionMessage)
            }

            exitProcess(1)
        }
    }
}
