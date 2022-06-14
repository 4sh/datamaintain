package datamaintain.db.driver.mongo

import datamaintain.db.driver.mongo.exception.DatamaintainMongoJsonMapperInstanceException
import datamaintain.core.exception.DatamaintainBuilderMandatoryException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*
import java.nio.file.Paths
import java.util.*

internal class MongoDriverConfigTest {

    @Test
    fun `should load mongo driver config`() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        expectThat(MongoDriverConfig.buildConfig(props)).and {
            get { uri }.isEqualTo("mongodb://localhost:27017/test-datamaintain")
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
        System.setProperty("db.uri", updatedURI)

        expectThat(MongoDriverConfig.buildConfig(props)).and {
            get { uri }.isEqualTo(updatedURI)
            get { tmpFilePath }.isEqualTo(Paths.get("/tmp/test"))
        }
    }

    @Test
    fun `should load FakeJsonMapper`() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        // Add fake mapper
        System.setProperty(MongoConfigKey.DB_MONGO_JSON_MAPPER.key, FakeJsonMapper::class.java.name)

        expectThat(MongoDriverConfig.buildConfig(props)).and {
            get { jsonMapper }.and {
                isNotNull()
                isA<FakeJsonMapper>()
            }
        }
    }

    @Test
    fun `should thrown a DatamaintainException when loading FakeJsonMapperWithBadConstructor`() {
        val props = Properties()
        props.load(MongoDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        // Add fake mapper
        System.setProperty(MongoConfigKey.DB_MONGO_JSON_MAPPER.key, FakeJsonMapperWithBadConstructor::class.java.name)

        expectThrows<DatamaintainMongoJsonMapperInstanceException> { MongoDriverConfig.buildConfig(props) }
    }

    @Nested
    inner class BuilderTest {
        @Test
        fun `should build config with builder`() {
            val config = MongoDriverConfig.Builder()
                .withUri("uri")
                .withSaveOutput(true)
                .withTrustUri(true)
                .withPrintOutput(true)
                .withTmpFilePath(Paths.get("/tmpFile"))
                .withMongoShell(MongoShell.MONGOSH)
                .withClientPath(Paths.get("/clientPath"))
                .withJsonMapper(FakeJsonMapper())
                .build()

            expectThat(config).and {
                get { uri } isEqualTo "uri"
                get { saveOutput }.isTrue()
                get { trustUri }.isTrue()
                get { printOutput }.isTrue()
                get { tmpFilePath } isEqualTo Paths.get("/tmpFile")
                get { mongoShell } isEqualTo MongoShell.MONGOSH
                get { clientPath } isEqualTo Paths.get("/clientPath")
                get { jsonMapper }.isA<FakeJsonMapper>()
            }
        }

        @Test
        fun `should build with default config`() {
            val config = MongoDriverConfig.Builder()
                .withUri("uri")
                .build()

            expectThat(config).and {
                get { uri } isEqualTo "uri"
                get { saveOutput }.isFalse()
                get { trustUri }.isFalse()
                get { printOutput }.isFalse()
                get { tmpFilePath } isEqualTo Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!)
                get { mongoShell } isEqualTo MongoShell.MONGO
                get { clientPath } isEqualTo Paths.get(MongoShell.MONGO.defaultBinaryName())
                get { jsonMapper }.isNull()
            }
        }

        @Test
        fun `should raise error because uri is not set in builder`() {
            expectThrows<DatamaintainBuilderMandatoryException>{ MongoDriverConfig.Builder().build() }
                .get { message } isEqualTo "Cannot build MongoDriverConfigBuilder : uri is mandatory"
        }
    }
}

