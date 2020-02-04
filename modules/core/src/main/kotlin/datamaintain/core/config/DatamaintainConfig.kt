package datamaintain.core.config

import datamaintain.core.config.ConfigKey.Companion.overrideBySystemProperties
import datamaintain.core.config.CoreConfigKey.*
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.script.Tag
import datamaintain.core.step.executor.ExecutionMode
import mu.KotlinLogging
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class DatamaintainConfig(val path: Path = Paths.get(SCAN_PATH.default!!),
                              val identifierRegex: Regex = Regex(SCAN_IDENTIFIER_REGEX.default!!),
                              val doesCreateTagsFromFolder: Boolean = CREATE_TAGS_FROM_FOLDER.default!!.toBoolean(),
                              val blacklistedTags: Set<Tag> = setOf(),
                              val executionMode: ExecutionMode = defaultExecutionMode,
                              val driverConfig: DatamaintainDriverConfig) {

    companion object {
        private val defaultExecutionMode = ExecutionMode.NORMAL

        @JvmStatic
        fun buildConfig(configInputStream: InputStream, driverConfig: DatamaintainDriverConfig): DatamaintainConfig {
            val props = Properties()
            props.load(configInputStream)
            return buildConfig(driverConfig, props)
        }

        @JvmStatic
        fun buildConfig(driverConfig: DatamaintainDriverConfig, props: Properties = Properties()): DatamaintainConfig {
            overrideBySystemProperties(props, values().asList())
            return DatamaintainConfig(
                    Paths.get(props.getProperty(SCAN_PATH)),
                    Regex(props.getProperty(SCAN_IDENTIFIER_REGEX)),
                    props.getProperty(CREATE_TAGS_FROM_FOLDER).toBoolean(),
                    props.getNullableProperty(TAGS_BLACKLISTED)?.split(",")
                            ?.map { Tag(it) }
                            ?.toSet()
                            ?: setOf(),
                    ExecutionMode.fromNullable(props.getNullableProperty(EXECUTION_MODE), defaultExecutionMode),
                    driverConfig)
        }
    }

    fun log() {
        logger.info { "Configuration: " }
        path.let { logger.info { "- path -> $path" } }
        identifierRegex.let { logger.info { "- identifier regex -> ${identifierRegex.pattern}" } }
        blacklistedTags.let { logger.info { "- blacklisted tags -> $blacklistedTags" } }
        executionMode.let { logger.info { "- execution mode -> $executionMode" } }
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
    // SCAN
    SCAN_PATH("scan.path", "./scripts/"),
    SCAN_IDENTIFIER_REGEX("scan.identifier.regex", ".*"),
    CREATE_TAGS_FROM_FOLDER("scan.tags.createFromFolder", "false"),

    // FILTER
    TAGS_BLACKLISTED("filter.tags.blacklisted"),


    // EXECUTE
    EXECUTION_MODE("execute.mode", "NORMAL"),
    FORCE_MARK_AS_EXECUTED("mark.as.executed", "false")
}

fun Properties.getProperty(configKey: ConfigKey): String =
        if (configKey.default != null) {
            getProperty(configKey, configKey.default!!)
        } else {
            getNullableProperty(configKey) ?: throw IllegalArgumentException("$configKey is mandatory")
        }

fun Properties.getNullableProperty(configKey: ConfigKey): String? = this.getProperty(configKey.key)

fun Properties.getProperty(configKey: ConfigKey, defaultValue: String): String =
        this.getProperty(configKey.key, defaultValue)


