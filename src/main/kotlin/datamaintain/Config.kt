package datamaintain

import datamaintain.db.drivers.DatamaintainDriver
import datamaintain.db.drivers.FakeDatamaintainDriver
import datamaintain.db.drivers.MongoDatamaintainDriver
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class Config(val path: Path,
             val identifierRegex: Regex,
             val mongoUri: String,
             val dbName: String) {
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
    // IDENTIFIER
    IDENTIFIER_REGEX("identifier.regex"),

    // DB
    DB_MONGO_URI("db.mongo.uri"),
    DB_MONGO_DBNAME("db.mongo.dbname"),

    // SCAN
    SCAN_PATH("scan.path"),
}

private fun Properties.toConfig(): Config {
    val path = this.getProperty(ConfigKey.SCAN_PATH)

    val config = Config(Paths.get(path),
            Regex(this.getProperty(ConfigKey.IDENTIFIER_REGEX)),
            this.getProperty(ConfigKey.DB_MONGO_URI),
            this.getProperty(ConfigKey.DB_MONGO_DBNAME))

    return config
}

private fun Properties.getProperty(configKey: ConfigKey): String {
    return this.getProperty(configKey.key)
}
