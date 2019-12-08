package datamaintain.db.driver.mongo

import datamaintain.core.driverLoaders
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotNull
import java.util.*



internal class MongoDriverLoaderTest {

    @Test
    fun `should load mongo driver`() {
        MongoDriverLoader.load()

        val props = Properties()
        props.load(MongoDriverLoaderTest::class.java.getResourceAsStream("/config/default.properties"))

        val driver = driverLoaders["mongo"]?.invoke(props)

        expectThat(driver).isNotNull()
    }
}