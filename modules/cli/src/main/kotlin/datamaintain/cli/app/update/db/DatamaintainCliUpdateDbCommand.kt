package datamaintain.cli.app.update.db

import com.github.ajalt.clikt.output.TermUi.echo
import datamaintain.cli.app.DatamaintainCliCommand
import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainException
import kotlin.system.exitProcess

fun defaultUpdateDbRunner(config: DatamaintainConfig, porcelain: Boolean) {
    val report = Datamaintain(config).updateDatabase()

    if (porcelain) {
        report.executedScripts.asSequence()
            .map { it.name }
            .forEach { echo(it) }  // Use echo because logger level is ERROR
    } else {
        report.print()
    }
}

abstract class DatamaintainCliUpdateDbCommand(
    name: String,
    val runner: (DatamaintainConfig, Boolean) -> Unit,
    help: String = ""
): DatamaintainCliCommand(name, help) {
    override fun executeCommand(config: DatamaintainConfig, porcelain: Boolean) {
        try {
            runner(config, porcelain)
        } catch (e: DatamaintainException) {
            echo("Error at step ${e.step}", err = true)
            e.report.print()
            echo("")
            echo(e.message, err = true)

            if (e.resolutionMessage.isNotEmpty()) {
                echo(e.resolutionMessage)
            }

            exitProcess(1)
        }
    }
}
