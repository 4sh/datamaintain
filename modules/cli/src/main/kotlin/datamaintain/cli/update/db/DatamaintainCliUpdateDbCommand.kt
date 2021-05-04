package datamaintain.cli.update.db

import datamaintain.cli.DatamaintainCliCommand
import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainException
import kotlin.system.exitProcess

fun defaultUpdateDbRunner(config: DatamaintainConfig) {
    Datamaintain(config).updateDatabase().print(config.verbose)
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
            val verbose: Boolean = config.verbose

            echo("Error at step ${e.step}", err = true)
            e.report.print(verbose)
            echo("")
            echo(e.message, err = true)

            if (e.resolutionMessage.isNotEmpty()) {
                echo(e.resolutionMessage)
            }

            exitProcess(1)
        }
    }
}