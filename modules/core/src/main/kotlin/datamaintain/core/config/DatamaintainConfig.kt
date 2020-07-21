package datamaintain.core.config

import datamaintain.core.config.ConfigKey.Companion.overrideBySystemProperties
import datamaintain.core.config.CoreConfigKey.*
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.script.Tag
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.executor.ExecutionMode
import mu.KotlinLogging
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class DatamaintainConfig @JvmOverloads constructor(val path: Path = Paths.get(SCAN_PATH.default!!),
                                                        val identifierRegex: Regex = Regex(SCAN_IDENTIFIER_REGEX.default!!),
                                                        val doesCreateTagsFromFolder: Boolean = CREATE_TAGS_FROM_FOLDER.default!!.toBoolean(),
                                                        val blacklistedTags: Set<Tag> = setOf(),
                                                        val tagsToPlayAgain: Set<Tag> = setOf(),
                                                        val tagsMatchers: Set<TagMatcher> = setOf(),
                                                        val executionMode: ExecutionMode = defaultExecutionMode,
                                                        val driverConfig: DatamaintainDriverConfig,
                                                        val verbose: Boolean = VERBOSE.default!!.toBoolean()) {

    companion object {
        private val defaultExecutionMode = ExecutionMode.NORMAL

        @JvmStatic
        fun buildConfig(configInputStream: InputStream, driverConfig: DatamaintainDriverConfig): DatamaintainConfig {
            val props = Properties()
            props.load(configInputStream)
            return buildConfig(driverConfig, props)
        }

        @JvmStatic
        @JvmOverloads
        fun buildConfig(driverConfig: DatamaintainDriverConfig, props: Properties = Properties()): DatamaintainConfig {
            overrideBySystemProperties(props, values().asList())
            return DatamaintainConfig(
                    Paths.get(props.getProperty(SCAN_PATH)),
                    Regex(props.getProperty(SCAN_IDENTIFIER_REGEX)),
                    props.getProperty(CREATE_TAGS_FROM_FOLDER).toBoolean(),
                    extractTags(props.getNullableProperty(TAGS_BLACKLISTED)),
                    extractTags(props.getNullableProperty(PRUNE_TAGS_TO_PLAY_AGAIN)),
                    props.getStringPropertiesByPrefix(TAG.key)
                            .map { TagMatcher.parse(it.first.replace("${TAG.key}.", ""), it.second) }
                            .toSet(),
                    ExecutionMode.fromNullable(props.getNullableProperty(EXECUTION_MODE), defaultExecutionMode),
                    driverConfig,
                    props.getProperty(VERBOSE).toBoolean()
            )
        }

        private fun extractTags(tags: String?): Set<Tag> {
            return tags?.split(",")
                    ?.map { Tag(it) }
                    ?.toSet()
                    ?: setOf()
        }
    }

    fun log() {
        logger.info { "Configuration: " }
        path.let { logger.info { "- path -> $it" } }
        identifierRegex.let { logger.info { "- identifier regex -> ${it.pattern}" } }
        blacklistedTags.let { logger.info { "- blacklisted tags -> $it" } }
        tagsToPlayAgain.let { logger.info { "- tags to play again -> $it" } }
        tagsMatchers.let { logger.info { "- tags -> $tagsMatchers" } }
        executionMode.let { logger.info { "- execution mode -> $it" } }
        verbose.let { logger.info { "- verbose -> $it" } }
        logger.info { "" }
    }

}

interface ConfigKey {
    val key: String
    val default: String?

    companion object {
        fun overrideBySystemProperties(props: Properties, configKeys: List<ConfigKey>) {
            configKeys.forEach { configKey ->
                val property: String? = System.getProperty(configKey.key)
                property?.let { props.put(configKey.key, it) }
            }
        }
    }
}


enum class CoreConfigKey(override val key: String,
                         override val default: String? = null) : ConfigKey {
    // GLOBAL
    VERBOSE("verbose", "false"),

    // SCAN
    SCAN_PATH("scan.path", "./scripts/"),
    SCAN_IDENTIFIER_REGEX("scan.identifier.regex", "(.*)"),
    CREATE_TAGS_FROM_FOLDER("scan.tags.createFromFolder", "false"),
    TAG("tag", ""),

    // FILTER
    TAGS_BLACKLISTED("filter.tags.blacklisted"),

    // PRUNER
    PRUNE_TAGS_TO_PLAY_AGAIN("prune.tags.to.play.again"),

    // EXECUTE
    EXECUTION_MODE("execute.mode", "NORMAL")
}

fun Properties.getProperty(configKey: ConfigKey): String =
        if (configKey.default != null) {
            getProperty(configKey, configKey.default!!)
        } else {
            getNullableProperty(configKey) ?: throw IllegalArgumentException("$configKey is mandatory")
        }

fun Properties.getNullableProperty(configKey: ConfigKey): String? = this.getProperty(configKey.key)

fun Properties.getStringPropertiesByPrefix(prefix: String): Set<Pair<String, String>> {
    return this.entries
            .filter { (it.key as String).startsWith(prefix) }
            .map { Pair(it.key as String, it.value as String) }
            .toSet()
}

fun Properties.getProperty(configKey: ConfigKey, defaultValue: String): String =
        this.getProperty(configKey.key, defaultValue)


