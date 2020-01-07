package datamaintain.db.driver.mongo

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Paths
import java.util.*

internal class MongoDriverConfigTest {

    @Test
    fun `should load mongo driver config`() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        expectThat(MongoDriverConfig.buildConfig(props)).and {
            get { dbName }.isEqualTo("test-datamaintain")
            get { mongoUri }.isEqualTo("mongodb://localhost:27017")
            get { tmpFilePath }.isEqualTo(Paths.get("/tmp/test"))
        }
    }

    @Test
    fun `should be overridden by jvm`() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        System.setProperty("db.mongo.dbname", "newName")

        expectThat(MongoDriverConfig.buildConfig(props)).and {
            get { dbName }.isEqualTo("newName")
            get { mongoUri }.isEqualTo("mongo://localhost:27017")
            get { tmpFilePath }.isEqualTo(Paths.get("/tmp/test"))
        }
    }
}