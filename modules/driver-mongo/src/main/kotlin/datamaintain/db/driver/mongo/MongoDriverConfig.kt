package datamaintain.db.driver.mongo

import datamaintain.core.ConfigKey
import datamaintain.core.getProperty
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

data class MongoDriverConfig(val dbName: String,
                             val mongoUri: String,
                             val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!)
) {
    companion object {
        fun buildConfig(props: Properties): MongoDriverConfig {
            return MongoDriverConfig(
                    props.getProperty(MongoConfigKey.DB_MONGO_DBNAME),
                    props.getProperty(MongoConfigKey.DB_MONGO_URI),
                    props.getProperty(MongoConfigKey.DB_MONGO_TMP_PATH).let { Paths.get(it) }
            )

        }
    }

    fun toDriver() = MongoDriver(
            dbName,
            mongoUri,
            tmpFilePath
    )
}

enum class MongoConfigKey(
        override val key: String,
        override val default: String? = null
) : ConfigKey {
    DB_MONGO_URI("db.mongo.uri"),
    DB_MONGO_DBNAME("db.mongo.dbname"),
    DB_MONGO_TMP_PATH("db.mongo.tmp.path", "/tmp/datamaintain.tmp"),
}