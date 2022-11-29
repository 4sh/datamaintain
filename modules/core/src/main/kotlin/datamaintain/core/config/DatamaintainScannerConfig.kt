package datamaintain.core.config

import datamaintain.core.script.TagMatcher
import mu.KotlinLogging
import java.nio.file.Path
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

data class DatamaintainScannerConfig @JvmOverloads constructor(
    val path: Path = Paths.get(CoreConfigKey.SCAN_PATH.default!!),
    val identifierRegex: Regex = Regex(CoreConfigKey.SCAN_IDENTIFIER_REGEX.default!!),
    val doesCreateTagsFromFolder: Boolean = CoreConfigKey.CREATE_TAGS_FROM_FOLDER.default!!.toBoolean(),
    val tagsMatchers: Set<TagMatcher> = setOf(),
) {
    fun log() {
        path.let { logger.debug { "- path -> $it" } }
        identifierRegex.let { logger.debug { "- identifier regex -> $it" } }
        doesCreateTagsFromFolder.let { logger.debug { "- create tags from folder -> $it" } }
        tagsMatchers.let { logger.debug { "- tags -> $it" } }
    }
}
