package datamaintain.core

import datamaintain.core.script.Tag
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Paths

class ConfigTest {
    @Test
    fun `should build config from resource`() {
        val expectedPath = Paths.get("/tmp/test")

        expectThat(Config.buildConfig(ConfigTest::class.java.getResourceAsStream("/config/default.properties"))).and {
            get { path }.isEqualTo(expectedPath)
            get { identifierRegex.pattern }.isEqualTo("(.*?)_.*")
            get { blacklistedTags }.isEqualTo(setOf(Tag("un"), Tag("deux")))
            get { dbDriverName }.isEqualTo("mongo")
        }
    }

    @Test
    fun `should contain default values`() {
        expectThat(Config.buildConfig(ConfigTest::class.java.getResourceAsStream("/config/minimal.properties"))) and {
            get { identifierRegex.pattern }.isEqualTo(Config.DEFAULT_IDENTIFIER_REGEX)
        }
    }
}
