package datamaintain.cli.app.utils

import java.util.Properties

/**
 * An enum used to define specific keys that can be used by commands for internal behaviour
 */
enum class CliSpecificKey(
    val key: String,
    val default: String?
) {
    // Each commands need to manage that in order to only print the effective configuration (so not execute the action)
    __PRINT_CONFIG_ONLY("__PRINT_CONFIG_ONLY", "false"),
    VERSION("version", "dev"),
    VERBOSE("verbose", "false"),
    TRACE("trace", "false"),
    PORCELAIN("porcelain", "false"),
}

fun Properties.getCliProperty(cliKey: CliSpecificKey) = this.getProperty(cliKey.key, cliKey.default)!!

fun Properties.getBooleanCliProperty(cliKey: CliSpecificKey) = this.getCliProperty(cliKey).toBoolean()

