package datamaintain

import datamaintain.db.drivers.DatamaintainDriver
import datamaintain.db.drivers.FakeDatamaintainDriver
import datamaintain.db.drivers.MongoDatamaintainDriver
import java.nio.file.Path

class Config(val path: Path,
             private val dbName: String,
             private val mongoUri: String) {
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
}
