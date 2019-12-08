package datamaintain.db.driver.mongo

import datamaintain.core.ConfigKey
import datamaintain.core.getProperty
import datamaintain.core.loadDriver


object MongoDriverLoader {

    enum class MongoConfigKey(override val key: String) : ConfigKey {
        DB_MONGO_URI("db.mongo.uri"),
        DB_MONGO_DBNAME("db.mongo.dbname"),
    }

    fun load() {
        loadDriver("mongo") { props ->
            MongoDatamaintainDriver(
                    props.getProperty(MongoConfigKey.DB_MONGO_DBNAME),
                    props.getProperty(MongoConfigKey.DB_MONGO_URI)
            )
        }
    }
}
