package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DBType
import datamaintain.db.driver.jdbc.JdbcDriverConfig
import datamaintain.db.driver.jdbc.JdbcDriverConfigTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

class DatamaintainConfigTest {
    @Test
    fun defaultFilenameRegex() {
        val props = Properties()
        props.load(JdbcDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))
        val jdbcDriverConfig = JdbcDriverConfig.buildConfig(props)

        val datamaintainConfig = DatamaintainConfig(driverConfig = jdbcDriverConfig)

        expectThat(datamaintainConfig.filenameRegex).and {
            isEqualTo(DBType.JDBC.filenameRegex)
        }
    }
}
