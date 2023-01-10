package datamaintain.db.driver.jdbc

import datamaintain.core.exception.DatamaintainBuilderMandatoryException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.util.*

internal class JdbcDriverConfigTest {

    @Test
    fun `should load jdbc driver config`() {
        val props = Properties()
        props.load(JdbcDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        expectThat(JdbcDriverConfig.buildConfig(props)).and {
            get { uri }.isEqualTo("jdbc:h2:mem:")
            get { trustUri }.isTrue()
        }
    }

    @Test
    fun `should be overridden by jvm`() {
        val props = Properties()
        props.load(JdbcDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        val updatedURI = "jdbc://localhost/my-db"
        System.setProperty("db.uri", updatedURI)
        System.setProperty("db.trust.uri", "false")
        System.setProperty("db.executed.scripts.storage.name", "myStorageName")

        expectThat(JdbcDriverConfig.buildConfig(props)).and {
            get { uri }.isEqualTo(updatedURI)
            get { trustUri }.isFalse()
            get { executedScriptsStorageName } isEqualTo "myStorageName"
        }
    }

    @Nested
    inner class BuilderTest {
        @Test
        fun `should build config with builder`() {
            val config = JdbcDriverConfig.Builder()
                .withUri("uri")
                .withSaveOutput(true)
                .withTrustUri(true)
                .withPrintOutput(true)
                .withExecutedScriptsStorageName("myStorageName")
                .build()

            expectThat(config).and {
                get { uri } isEqualTo "uri"
                get { saveOutput }.isTrue()
                get { trustUri }.isTrue()
                get { printOutput }.isTrue()
                get { executedScriptsStorageName } isEqualTo "myStorageName"
            }
        }

        @Test
        fun `should build with default config`() {
            val config = JdbcDriverConfig.Builder()
                .withUri("uri")
                .build()

            expectThat(config).and {
                get { uri } isEqualTo "uri"
                get { saveOutput }.isFalse()
                get { trustUri }.isFalse()
                get { printOutput }.isFalse()
                get { executedScriptsStorageName } isEqualTo "executedScripts"
            }
        }

        @Test
        fun `should raise error because uri is not set in builder`() {
            expectThrows<DatamaintainBuilderMandatoryException>{ JdbcDriverConfig.Builder().build() }
                .get { message } isEqualTo "Cannot build JdbcDriverConfigBuilder : uri is mandatory"
        }
    }
}
