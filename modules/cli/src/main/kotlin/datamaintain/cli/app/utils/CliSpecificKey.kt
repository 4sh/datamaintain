package datamaintain.cli.app.utils

/**
 * An enum used to define specific keys that can be used by commands for internal behaviour
 */
enum class CliSpecificKey {
    // Each commands need to manage that in order to only print the effective configuration (so not execute the action)
    __PRINT_CONFIG_ONLY
}