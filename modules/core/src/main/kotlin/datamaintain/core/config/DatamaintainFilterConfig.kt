package datamaintain.core.config

import datamaintain.core.script.Tag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class DatamaintainFilterConfig @JvmOverloads constructor(
    val whitelistedTags: Set<Tag> = setOf(),
    val blacklistedTags: Set<Tag> = setOf(),
) {
    fun log() {
        whitelistedTags.let { logger.info { "- whitelisted tags -> $it" } }
        blacklistedTags.let { logger.info { "- blacklisted tags -> $it" } }
    }
}

