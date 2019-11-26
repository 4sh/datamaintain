package datamaintain

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Paths

class ConfigTest {
    @Test
    fun `should build config from resource`() {
        val expectedPath = Paths.get("/tmp/test")

        expectThat(Config.buildConfigFromResource("/config/default.properties")).and {
           get { path }.isEqualTo(expectedPath)
           get { mongoUri }.isEqualTo("mongo://localhost:27017")
           get { dbName }.isEqualTo("test-datamaintain")
           get { identifierRegex.pattern }.isEqualTo("(.*?)_.*")
        }
    }

    @Test
    fun `should contain default values`() {
        expectThat(Config.buildConfigFromResource("/config/minimal.properties")).and {
            get { identifierRegex.pattern }.isEqualTo(Config.DEFAULT_IDENTIFIER_REGEX)
        }
    }
}
