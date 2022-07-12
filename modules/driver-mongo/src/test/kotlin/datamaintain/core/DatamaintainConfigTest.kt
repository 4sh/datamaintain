package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DBType
import datamaintain.db.driver.mongo.MongoDriverConfig
import datamaintain.db.driver.mongo.MongoDriverConfigTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

class DatamaintainConfigTest {
    @Test
    fun defaultFilenameRegex() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))
        val mongoDriverConfig = MongoDriverConfig.buildConfig(props)

        val datamaintainConfig = DatamaintainConfig(driverConfig = mongoDriverConfig)

        expectThat(datamaintainConfig.filenameRegex).and {
            isEqualTo(DBType.MONGO.filenameRegex)
        }
    }
}
