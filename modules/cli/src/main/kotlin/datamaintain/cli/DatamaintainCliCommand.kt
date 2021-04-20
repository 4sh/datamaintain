package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import datamaintain.cli.utils.loadConfig
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainBaseException
import java.util.*
import kotlin.system.exitProcess

abstract class DatamaintainCliCommand(name: String) : CliktCommand(name = name) {
    private val props by findObject { Properties() }

    override fun run() {
        try {
            overloadPropsFromArgs(props)
            val config = loadConfig(props)
            executeCommand(config)
        } catch (e: DatamaintainBaseException) {
            echo(e.message, err = true)
            echo(e.resolutionMessage)
            exitProcess(1)
        } catch (e: IllegalArgumentException) {
            echo(e.message, err = true)
            exitProcess(1)
        } catch (e: Exception) {
            echo(e.message ?: "unexpected error", err = true)
            exitProcess(1)
        }
    }

    protected abstract fun overloadPropsFromArgs(props: Properties)

    protected abstract fun executeCommand(config: DatamaintainConfig)
}