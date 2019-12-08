package datamaintain.core

import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDatamaintainDriver
import datamaintain.core.script.Tag
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class Config(val path: Path,
             val identifierRegex: Regex,
             val blacklistedTags: Set<Tag> = setOf(),
             val dbDriver: DatamaintainDriver) {

    companion object {
        const val DEFAULT_IDENTIFIER_REGEX = ".*"

        fun buildConfigFromResource(resource: String): Config {
            return buildConfig(Config::class.java.getResourceAsStream(resource))
        }

        fun buildConfig(configInputstream: InputStream): Config {
            val props = Properties()

            props.load(configInputstream)

            val path = props.getProperty(CoreConfigKey.SCAN_PATH)

            return Config(Paths.get(path),
                    Regex(props.getProperty(CoreConfigKey.SCAN_IDENTIFIER_REGEX, Config.DEFAULT_IDENTIFIER_REGEX)),
                    props.getNullableProperty(CoreConfigKey.TAGS_BLACKLISTED)?.split(",")
                            ?.map { Tag(it) }
                            ?.toSet()
                            ?: setOf(),
                    driverLoader?.invoke(props) ?: FakeDatamaintainDriver())

        }
    }
}

interface ConfigKey {
    val key: String
}

enum class CoreConfigKey(override val key: String) : ConfigKey {
    TAGS_BLACKLISTED("tags.blacklisted"),

    // SCAN
    SCAN_PATH("scan.path"),
    SCAN_IDENTIFIER_REGEX("scan.identifier.regex")
}

fun Properties.getProperty(configKey: ConfigKey): String {
    return getNullableProperty(configKey) ?: throw IllegalArgumentException("$configKey is mandatory")
}

fun Properties.getNullableProperty(configKey: ConfigKey): String? {
    return this.getProperty(configKey.key)
}

fun Properties.getProperty(configKey: ConfigKey, defaultValue: String): String {
    return this.getProperty(configKey.key, defaultValue)
}


private var driverLoader: ((Properties) -> DatamaintainDriver)? = null

fun loadDriver(_driverLoader: (Properties) -> DatamaintainDriver) {
    driverLoader = _driverLoader
}
