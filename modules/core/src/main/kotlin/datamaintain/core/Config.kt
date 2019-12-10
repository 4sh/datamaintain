package datamaintain.core

import datamaintain.core.script.Tag
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

data class Config(val path: Path = Paths.get(CoreConfigKey.SCAN_PATH.default),
                  val identifierRegex: Regex = Regex(CoreConfigKey.SCAN_IDENTIFIER_REGEX.default!!),
                  val blacklistedTags: Set<Tag> = setOf(),
                  val dbDriverName: String? = null) {

    companion object {

        fun buildConfig(configInputStream: InputStream): Config {
            val props = Properties()
            props.load(configInputStream)
            return buildConfig(props)
        }

        fun buildConfig(props: Properties): Config {
            return Config(
                    Paths.get(props.getProperty(CoreConfigKey.SCAN_PATH)),
                    Regex(props.getProperty(CoreConfigKey.SCAN_IDENTIFIER_REGEX)),
                    props.getNullableProperty(CoreConfigKey.TAGS_BLACKLISTED)?.split(",")
                            ?.map { Tag(it) }
                            ?.toSet()
                            ?: setOf(),
                    props.getNullableProperty(CoreConfigKey.DB_DRIVER))
        }

    }
}

interface ConfigKey {
    val key: String
    val default: String?
}

enum class CoreConfigKey(override val key: String,
                         override val default: String? = null) : ConfigKey {
    TAGS_BLACKLISTED("tags.blacklisted"),

    // DRIVER
    DB_DRIVER("db.driver"),

    // SCAN
    SCAN_PATH("scan.path", "./scripts/"),
    SCAN_IDENTIFIER_REGEX("scan.identifier.regex", ".*")
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


