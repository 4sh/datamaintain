package datamaintain.cli.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import datamaintain.cli.app.utils.*
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

            val isPrintConfigOnly = props.getBooleanCliProperty(CliSpecificKey.__PRINT_CONFIG_ONLY)
            if (isPrintConfigOnly) {
                // config log nothing in INFO level so apply DEBUG level
                applyLoggerLevel(CliDatamaintainLoggerLevel.VERBOSE)
                config.log()
            } else {
                val level = if (props.getBooleanCliProperty(CliSpecificKey.TRACE)) {
                    CliDatamaintainLoggerLevel.TRACE
                } else if (props.getBooleanCliProperty(CliSpecificKey.VERBOSE)) {
                    CliDatamaintainLoggerLevel.VERBOSE
                } else if (props.getBooleanCliProperty(CliSpecificKey.PORCELAIN)) {
                    CliDatamaintainLoggerLevel.PORCELAIN
                } else {
                    CliDatamaintainLoggerLevel.INFO
                }
                applyLoggerLevel(level)

                executeCommand(config, level == CliDatamaintainLoggerLevel.PORCELAIN)
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

    protected abstract fun executeCommand(config: DatamaintainConfig, porcelain: Boolean)
}
