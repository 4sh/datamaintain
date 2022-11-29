package datamaintain.cli.app.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory

/**
 * Translate cli level to logback level and set that level on datamaintain loggers
 */
fun applyLoggerLevel(level: CliDatamaintainLoggerLevel) {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    val datamaintainLogger = context.getLogger("datamaintain")

    when(level) {
        CliDatamaintainLoggerLevel.INFO -> datamaintainLogger.level = Level.INFO
        CliDatamaintainLoggerLevel.VERBOSE -> datamaintainLogger.level = Level.DEBUG
        CliDatamaintainLoggerLevel.TRACE -> datamaintainLogger.level = Level.TRACE
        CliDatamaintainLoggerLevel.PORCELAIN -> datamaintainLogger.level = Level.ERROR
        else -> error("Cannot set log level of $level")
    }
}

enum class CliDatamaintainLoggerLevel {
    // normal log level
    INFO,

    // debug log level
    VERBOSE,

    // trace log level
    TRACE,

    // special mode for cli, the datamaintain logs are disabled (except error) and specific logs are print.
    // This is use for scripts that may need to parse datamaintain logs (e.g: for take in account execution results)
    PORCELAIN
}
