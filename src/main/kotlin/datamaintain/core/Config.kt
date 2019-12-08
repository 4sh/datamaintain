package datamaintain.core

import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.FakeDatamaintainDriver
import datamaintain.core.script.Tag
import datamaintain.db.driver.mongo.MongoDatamaintainDriver
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class Config(val path: Path,
             val mongoUri: String,
             val dbName: String,
             val identifierRegex: Regex,
             val blacklistedTags: Set<Tag> = setOf()) {
    private var customDbDriver: DatamaintainDriver? = null

    val dbDriver: DatamaintainDriver
        get() {
            if (customDbDriver == null) {
                if (dbName.isNotEmpty() and mongoUri.isNotEmpty()) {
                    customDbDriver = MongoDatamaintainDriver(dbName, mongoUri)
                } else {
                    customDbDriver = FakeDatamaintainDriver()
                }
            }
            return customDbDriver!!
        }

    infix fun withDriver(other: DatamaintainDriver): Config {
        customDbDriver = other
        return this
    }

    companion object {
        const val DEFAULT_IDENTIFIER_REGEX = ".*"

        fun buildConfigFromResource(resource: String): Config {
            return buildConfig(Config::class.java.getResourceAsStream(resource))
        }

        fun buildConfig(configInputstream: InputStream): Config {
            val properties = Properties()

            properties.load(configInputstream)

            return properties.toConfig()
        }
    }
}

enum class ConfigKey(val key: String) {
    // DB
    DB_MONGO_URI("db.mongo.uri"),
    DB_MONGO_DBNAME("db.mongo.dbname"),
    TAGS_BLACKLISTED("tags.blacklisted"),

    // SCAN
    SCAN_PATH("scan.path"),
    SCAN_IDENTIFIER_REGEX("scan.identifier.regex")
}

private fun Properties.toConfig(): Config {
    val path = this.getProperty(ConfigKey.SCAN_PATH)

    val config = Config(Paths.get(path),
            this.getProperty(ConfigKey.DB_MONGO_URI),
            this.getProperty(ConfigKey.DB_MONGO_DBNAME),
            Regex(this.getProperty(ConfigKey.SCAN_IDENTIFIER_REGEX, Config.DEFAULT_IDENTIFIER_REGEX)),
            this.getNullableProperty(ConfigKey.TAGS_BLACKLISTED)?.split(",")
                    ?.map { Tag(it) }
                    ?.toSet()
                    ?: setOf())

    return config
}

private fun Properties.getProperty(configKey: ConfigKey): String {
    return getNullableProperty(configKey) ?: throw IllegalArgumentException("$configKey is mandatory")
}

private fun Properties.getNullableProperty(configKey: ConfigKey): String? {
    return this.getProperty(configKey.key)
}

private fun Properties.getProperty(configKey: ConfigKey, defaultValue: String): String {
    return this.getProperty(configKey.key, defaultValue)
}
