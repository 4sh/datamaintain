package datamaintain.core.config

import datamaintain.core.config.ConfigKey.Companion.overrideBySystemProperties
import datamaintain.core.config.CoreConfigKey.*
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.script.ScriptAction
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
                                                        val whitelistedTags: Set<Tag> = setOf(),
                                                        val blacklistedTags: Set<Tag> = setOf(),
                                                        val tagsToPlayAgain: Set<Tag> = setOf(),
                                                        val overrideExecutedScripts: Boolean = PRUNE_OVERRIDE_UPDATED_SCRIPTS.default!!.toBoolean(),
                                                        val tagsMatchers: Set<TagMatcher> = setOf(),
                                                        val checkRules: Sequence<String> = emptySequence(),
                                                        val executionMode: ExecutionMode = defaultExecutionMode,
                                                        val defaultScriptAction: ScriptAction = defaultAction,
                                                        val driverConfig: DatamaintainDriverConfig,
                                                        val verbose: Boolean = VERBOSE.default!!.toBoolean(),
                                                        val porcelain: Boolean = PRINT_RELATIVE_PATH_OF_SCRIPT.default!!.toBoolean(),
                                                        val flags: List<String> = emptyList()) {

    companion object {
        private val defaultExecutionMode = ExecutionMode.NORMAL
        public val defaultAction = ScriptAction.RUN

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

            var executionMode = ExecutionMode.fromNullable(props.getNullableProperty(EXECUTION_MODE), defaultExecutionMode)

            val scriptAction = ScriptAction.fromNullable(props.getNullableProperty(DEFAULT_SCRIPT_ACTION), defaultAction)

            val scanPath = Paths.get(props.getProperty(SCAN_PATH)).toAbsolutePath().normalize()
            return DatamaintainConfig(
                    scanPath,
                    Regex(props.getProperty(SCAN_IDENTIFIER_REGEX)),
                    props.getProperty(CREATE_TAGS_FROM_FOLDER).toBoolean(),
                    extractTags(props.getNullableProperty(TAGS_WHITELISTED)),
                    extractTags(props.getNullableProperty(TAGS_BLACKLISTED)),
                    extractTags(props.getNullableProperty(PRUNE_TAGS_TO_RUN_AGAIN)),
                    props.getProperty(PRUNE_OVERRIDE_UPDATED_SCRIPTS).toBoolean(),
                    props.getStringPropertiesByPrefix(TAG.key)
                            .map { TagMatcher.parse(it.first.replace("${TAG.key}.", ""), it.second, scanPath) }
                            .toSet(),
                    extractSequence(props.getNullableProperty(CHECK_RULES)),
                    executionMode,
                    scriptAction,
                    driverConfig,
                    props.getProperty(VERBOSE).toBoolean(),
                    props.getProperty(PRINT_RELATIVE_PATH_OF_SCRIPT).toBoolean(),
                    extractSequence(props.getNullableProperty(FLAGS)).toList()
            )
        }

        private fun extractTags(tags: String?): Set<Tag> {
            return tags?.split(",")
                    ?.map { Tag(it) }
                    ?.toSet()
                    ?: setOf()
        }

        private fun extractSequence(string: String?): Sequence<String> {
            return if (string.isNullOrEmpty()) {
                sequenceOf()
            } else {
                string.splitToSequence(",")
            }
        }
    }

    fun log() {
        logger.info { "Configuration: " }
        path.let { logger.info { "- path -> $it" } }
        defaultScriptAction.let { logger.info { "- script action -> ${it}" } }
        identifierRegex.let { logger.info { "- identifier regex -> ${it.pattern}" } }
        blacklistedTags.let { logger.info { "- blacklisted tags -> $it" } }
        tagsToPlayAgain.let { logger.info { "- tags to play again -> $it" } }
        tagsMatchers.let { logger.info { "- tags -> $tagsMatchers" } }
        overrideExecutedScripts.let { logger.info { "- Allow override executed script -> ${it}" } }
        checkRules.let { logger.info { "- rules -> ${checkRules.toList()}" } }
        executionMode.let { logger.info { "- execution mode -> $it" } }
        verbose.let { logger.info { "- verbose -> $it" } }
        porcelain.let { logger.info { "- porcelain -> $it" } }
        flags.let { logger.info { "- flags -> $it" } }
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
    DB_TYPE("db.type", "mongo"),
    VERBOSE("verbose", "false"),
    DEFAULT_SCRIPT_ACTION("default.script.action", "RUN"),
    PRINT_RELATIVE_PATH_OF_SCRIPT("porcelain", "false"),
    FLAGS("flags"),

    // SCAN
    SCAN_PATH("scan.path", "./scripts/"),
    SCAN_IDENTIFIER_REGEX("scan.identifier.regex", "(.*)"),
    CREATE_TAGS_FROM_FOLDER("scan.tags.createFromFolder", "false"),
    TAG("tag", ""),

    // FILTER
    TAGS_WHITELISTED("filter.tags.whitelisted"),
    TAGS_BLACKLISTED("filter.tags.blacklisted"),

    // PRUNER
    PRUNE_TAGS_TO_RUN_AGAIN("prune.tags.to.run.again"),
    PRUNE_OVERRIDE_UPDATED_SCRIPTS("prune.scripts.override.executed", "false"),

    // CHECKER
    CHECK_RULES("check.rules"),

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


