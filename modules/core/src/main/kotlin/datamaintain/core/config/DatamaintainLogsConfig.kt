package datamaintain.core.config

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class DatamaintainLogsConfig @JvmOverloads constructor(
    val verbose: Boolean = CoreConfigKey.VERBOSE.default!!.toBoolean(),
    val porcelain: Boolean = CoreConfigKey.PRINT_RELATIVE_PATH_OF_SCRIPT.default!!.toBoolean(),
) {
    fun log() {
        verbose.let { logger.info { "- verbose -> $it" } }
        porcelain.let { logger.info { "- porcelain -> $it" } }
    }
}
