package datamaintain.db.driver.jdbc

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Paths
import java.util.*

internal class JdbcDriverConfigTest {

    @Test
    fun `should load jdbc driver config`() {
        val props = Properties()
        props.load(JdbcDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        expectThat(JdbcDriverConfig.buildConfig(props)).and {
            get { jdbcUri }.isEqualTo("jdbc:postgresql://localhost:5432/test")
            get { tmpFilePath }.isEqualTo(Paths.get("/tmp/test"))
            get { printOutput }.isEqualTo(true)
            get { saveOutput }.isEqualTo(true)
        }
    }

    @Test
    fun `should be overridden by jvm`() {
        val props = Properties()
        props.load(JdbcDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        val updatedURI = "mongodb://localhost:27017/newName"
        System.setProperty("db.jdbc.uri", updatedURI)

        expectThat(JdbcDriverConfig.buildConfig(props)).and {
            get { jdbcUri }.isEqualTo(updatedURI)
            get { tmpFilePath }.isEqualTo(Paths.get("/tmp/test"))
        }
    }
}