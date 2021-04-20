package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import datamaintain.cli.utils.loadConfig
import datamaintain.cli.utils.logger
import datamaintain.core.Datamaintain
import datamaintain.core.exception.DatamaintainBaseException
import java.util.*
import kotlin.system.exitProcess

class ListExecutedScripts : CliktCommand(name = "list") {
    private val props by requireObject<Properties>()

    override fun run() {
        try {
            val config = loadConfig(props)
            Datamaintain(config).listExecutedScripts().forEach {
                logger.info { "${it.name} (${it.checksum})" }
            }
        } catch (e: DatamaintainBaseException) {
            echo(e.message, err = true)
            echo(e.resolutionMessage)

            exitProcess(1)
        } catch (e: IllegalArgumentException) {
            echo(e.message)
            exitProcess(1)
        } catch (e: Exception) {
            echo(e.message ?: "unexpected error")
            exitProcess(1)
        }
    }
}
