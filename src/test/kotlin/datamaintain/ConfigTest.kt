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
        }
    }
}
