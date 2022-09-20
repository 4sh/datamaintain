package datamaintain.db.driver.mongo

import datamaintain.core.exception.DatamaintainBuilderMandatoryException
import datamaintain.db.driver.mongo.exception.DatamaintainMongoClientNotFound
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.nio.file.Files
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
                .withClientExecutable("/clientPath")
                .build()

            expectThat(config).and {
                get { uri } isEqualTo "uri"
                get { saveOutput }.isTrue()
                get { trustUri }.isTrue()
                get { printOutput }.isTrue()
                get { tmpFilePath } isEqualTo Paths.get("/tmpFile")
                get { mongoShell } isEqualTo MongoShell.MONGOSH
                get { clientExecutable } isEqualTo "/clientPath"
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
                get { clientExecutable } isEqualTo MongoShell.MONGO.defaultBinaryName()
            }
        }

        @Test
        fun `should raise error because uri is not set in builder`() {
            expectThrows<DatamaintainBuilderMandatoryException>{ MongoDriverConfig.Builder().build() }
                .get { message } isEqualTo "Cannot build MongoDriverConfigBuilder : uri is mandatory"
        }
    }

    @Nested
    inner class ClientExecutableExist {
        @Test
        fun `should raise an exception because the path is invalid`() {
            println("test")
            val clientExecutable = "/path/not/exists/mongo"
            val mongoDriverConfig = MongoDriverConfig(
                "mongodb://localhost:27017",
                trustUri = true,
                clientExecutable = clientExecutable
            )

            mockkStatic(Files::class)
            every { Files.exists(any()) } returns false

            expectThrows<DatamaintainMongoClientNotFound>{ mongoDriverConfig.ensureMongoExecutableIsPresent() }
                .and {
                    get { message } isEqualTo "Cannot find $clientExecutable"
                    get { resolutionMessage } isEqualTo "Check your command : is '$clientExecutable --version' work ? " +
                            "If mongo client is a command, check your PATH variable. " +
                            "If mongo client is a path, please check the path exists."
                }
        }

        @AfterEach
        fun afterEach(){
            unmockkStatic(Files::class, System::class)
        }

        @Test
        fun `should raise an exception because the command is invalid`() {
            val clientExecutable = "mongo-not-exists"
            val mongoDriverConfig = MongoDriverConfig(
                "mongodb://localhost:27017",
                trustUri = true,
                clientExecutable = clientExecutable
            )

            mockkStatic(System::class)
            every { System.getenv("PATH") } returns "/test:/mongodb"

            mockkStatic(Files::class)
            every { Files.exists(Paths.get("/test/mongosh")) } returns false

            expectThrows<DatamaintainMongoClientNotFound>{ mongoDriverConfig.ensureMongoExecutableIsPresent() }
                .and {
                    get { message } isEqualTo "Cannot find $clientExecutable"
                    get { resolutionMessage } isEqualTo "Check your command : is '$clientExecutable --version' work ? " +
                            "If mongo client is a command, check your PATH variable. " +
                            "If mongo client is a path, please check the path exists."
                }
        }

        @Test
        fun `should accept a valid command`() {
            val clientExecutable = "mongosh"
            val mongoDriverConfig = MongoDriverConfig(
                "mongodb://localhost:27017",
                trustUri = true,
                clientExecutable = clientExecutable
            )

            mockkStatic(System::class)
            every { System.getenv("PATH") } returns "/test:/mongodb"

            mockkStatic(Files::class)
            every { Files.exists(Paths.get("/mongodb/mongosh")) } returns true
            every { Files.exists(Paths.get("/test/mongosh")) } returns false

            mongoDriverConfig.ensureMongoExecutableIsPresent()

            expectThat(mongoDriverConfig.clientExecutable) isEqualTo clientExecutable
        }

        @Test
        fun `should accept a path in current folder`() {
            val clientExecutable = "mongosh"
            val mongoDriverConfig = MongoDriverConfig(
                "mongodb://localhost:27017",
                trustUri = true,
                clientExecutable = clientExecutable
            )

            mockkStatic(System::class)
            every { System.getenv("PATH") } returns "/test:/mongodb"

            // all folders return false except the working directory
            mockkStatic(Files::class)
            every { Files.exists(Paths.get("/mongodb/mongosh")) } returns false
            every { Files.exists(Paths.get("/test/mongosh")) } returns false
            every { Files.exists(Paths.get("mongosh")) } returns true

            mongoDriverConfig.ensureMongoExecutableIsPresent()

            val expectedPath = Paths.get(clientExecutable).toAbsolutePath().toString()
            expectThat(mongoDriverConfig.clientExecutable) isEqualTo expectedPath
        }

        @Test
        fun `should accept a valid path`() {
            val clientExecutable = "/mongodb/mongosh"
            val mongoDriverConfig = MongoDriverConfig(
                "mongodb://localhost:27017",
                trustUri = true,
                clientExecutable = clientExecutable
            )

            mockkStatic(Files::class)
            every { Files.exists(Paths.get("/mongodb/mongosh")) } returns true

            mongoDriverConfig.ensureMongoExecutableIsPresent()

            expectThat(mongoDriverConfig.clientExecutable) isEqualTo clientExecutable
        }
    }
}
