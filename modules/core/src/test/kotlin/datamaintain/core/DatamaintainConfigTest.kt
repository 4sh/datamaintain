package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.Tag
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Paths

class DatamaintainConfigTest {
    @Test
    fun `should build config from resource`() {
        val expectedPath = Paths.get("/tmp/test")

        val config = DatamaintainConfig.buildConfig(DatamaintainConfigTest::class.java.getResourceAsStream("/config/default.properties"),
                FakeDriverConfig())
        expectThat(config).and {
            get { path }.isEqualTo(expectedPath)
            get { identifierRegex.pattern }.isEqualTo("(.*?)_.*")
            get { blacklistedTags }.isEqualTo(setOf(Tag("un"), Tag("deux")))
        }
    }

    @Test
    fun `should contain default values`() {
        val config = DatamaintainConfig.buildConfig(DatamaintainConfigTest::class.java.getResourceAsStream("/config/minimal.properties"),
                FakeDriverConfig())
        expectThat(config) and {
            get { identifierRegex.pattern }.isEqualTo(CoreConfigKey.SCAN_IDENTIFIER_REGEX.default)
        }
    }
}
