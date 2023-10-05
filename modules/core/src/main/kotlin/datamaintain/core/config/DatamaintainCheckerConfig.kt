package datamaintain.core.config

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class DatamaintainCheckerConfig @JvmOverloads constructor(
    val rules: List<String> = emptyList(),
) {
    fun log() {
        rules.let { logger.debug { "- rules -> $it" } }
    }
}

