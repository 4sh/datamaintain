package datamaintain.db.driver.jdbc

import org.junit.jupiter.api.Test
import strikt.api.expectThat
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
            get { jdbcUri }.isEqualTo("jdbc:h2:mem:")
            get { trustUri }.isTrue()
        }
    }

    @Test
    fun `should be overridden by jvm`() {
        val props = Properties()
        props.load(JdbcDriverConfigTest::class.java.getResourceAsStream("/config/default.properties"))

        val updatedURI = "jdbc://localhost/my-db"
        System.setProperty("db.jdbc.uri", updatedURI)
        System.setProperty("db.trust.uri", "false")

        expectThat(JdbcDriverConfig.buildConfig(props)).and {
            get { jdbcUri }.isEqualTo(updatedURI)
            get { trustUri }.isFalse()
        }
    }
}