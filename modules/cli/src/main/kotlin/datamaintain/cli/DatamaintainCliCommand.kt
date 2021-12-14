package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import datamaintain.cli.utils.CliSpecificKey
import datamaintain.cli.utils.loadConfig
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainBaseException
import java.util.*
import kotlin.system.exitProcess

abstract class DatamaintainCliCommand(name: String, help: String = "") : CliktCommand(name = name, help = help) {
    private val props by findObject { Properties() }

    override fun run() {
        try {
            overloadProps(props)
            val config = loadConfig(props)

            if (props.getProperty(CliSpecificKey.__PRINT_CONFIG_ONLY.name, "false")!!.toBoolean()) {
                config.log()
            } else {
                executeCommand(config)
            }
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

    protected abstract fun overloadProps(props: Properties)

    protected abstract fun executeCommand(config: DatamaintainConfig)
}