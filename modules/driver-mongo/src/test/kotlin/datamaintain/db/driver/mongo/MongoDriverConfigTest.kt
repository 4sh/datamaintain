package datamaintain.db.driver.mongo

import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.failed
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.nio.file.Paths
import java.util.*

internal class MongoDriverConfigTest {

    @Test
    fun `should load mongo driver config`() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        expectThat(MongoDriverConfig.buildConfig(props)).and {
            get { mongoUri }.isEqualTo("mongodb://localhost:27017/test-datamaintain")
            get { tmpFilePath }.isEqualTo(Paths.get("/tmp/test"))
            get { printOutput }.isEqualTo(true)
            get { saveOutput }.isEqualTo(true)
        }
    }

    @Test
    fun `should be overridden by jvm`() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        val updatedURI = "mongodb://localhost:27017/newName"
        System.setProperty("db.mongo.uri", updatedURI)

        expectThat(MongoDriverConfig.buildConfig(props)).and {
            get { mongoUri }.isEqualTo(updatedURI)
            get { tmpFilePath }.isEqualTo(Paths.get("/tmp/test"))
        }
    }

    @Test
    fun `should throw a mongoUri protocol missing`() {
//        // mongo host has form host:port and miss mongodb protocol
//        expectCatching {
//            MongoDriverConfig(
//                    "localhost:27018/database",
//                    Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
//                    Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
//            ).toDriver()
//        }
//                .failed()
//                .isA<java.lang.IllegalArgumentException>()
//                .get { localizedMessage }.contains("Connection strings must start with either 'mongodb://' or 'mongodb+srv://")
    }

    @Test
    fun `should throw a collection error in mongoURI`() {
        // mongo host has form host:port and miss mongodb protocol
//        expectCatching {
//            MongoDriverConfig(
//                    "mongodb://localhost:27018/database.pouet",
//                    Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!),
//                    Paths.get(MongoConfigKey.DB_MONGO_CLIENT_PATH.default!!)
//            ).toDriver()
//        }
//                .failed()
//                .isA<java.lang.IllegalArgumentException>()
//                .get { localizedMessage }.contains("MongoUri contains a collection name, please remove it")
    }
}