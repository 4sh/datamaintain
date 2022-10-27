package datamaintain.core.config

import datamaintain.core.script.Tag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class DatamaintainPrunerConfig @JvmOverloads constructor(
    val tagsToPlayAgain: Set<Tag> = setOf(),
) {
    fun log() {
        tagsToPlayAgain.let { logger.info { "- tags to play again -> $it" } }
    }
}

