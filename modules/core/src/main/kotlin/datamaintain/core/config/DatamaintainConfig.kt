package datamaintain.core.config

import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.script.Tag
import mu.KotlinLogging
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private val logger = KotlinLogging.logger {}

data class DatamaintainConfig(val path: Path = Paths.get(CoreConfigKey.SCAN_PATH.default),
                              val identifierRegex: Regex = Regex(CoreConfigKey.SCAN_IDENTIFIER_REGEX.default!!),
                              val blacklistedTags: Set<Tag> = setOf(),
                              val driverConfig: DatamaintainDriverConfig) {

    companion object {

        fun buildConfig(configInputStream: InputStream, driverConfig: DatamaintainDriverConfig): DatamaintainConfig {
            val props = Properties()
            props.load(configInputStream)
            return buildConfig(props, driverConfig)
        }

        fun buildConfig(props: Properties, driverConfig: DatamaintainDriverConfig): DatamaintainConfig {
            return DatamaintainConfig(
                    Paths.get(props.getProperty(CoreConfigKey.SCAN_PATH)),
                    Regex(props.getProperty(CoreConfigKey.SCAN_IDENTIFIER_REGEX)),
                    props.getNullableProperty(CoreConfigKey.TAGS_BLACKLISTED)?.split(",")
                            ?.map { Tag(it) }
                            ?.toSet()
                            ?: setOf(),
                    driverConfig)
        }

    }

    fun log() {
        logger.info { "configuration: " }
        path.let { logger.info { "- path -> $path" } }
        identifierRegex.let { logger.info { "- identifier regex -> ${identifierRegex.pattern}" } }
        blacklistedTags.let { logger.info { "- blacklisted tags -> $blacklistedTags" } }
        logger.info { "" }
    }

}

interface ConfigKey {
    val key: String
    val default: String?
}

enum class CoreConfigKey(override val key: String,
                         override val default: String? = null) : ConfigKey {
    // DRIVER
    DB_DRIVER("db.driver"),

    // SCAN
    SCAN_PATH("scan.path", "./scripts/"),
    SCAN_IDENTIFIER_REGEX("scan.identifier.regex", ".*"),

    // FILTER
    TAGS_BLACKLISTED("filter.tags.blacklisted")
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


