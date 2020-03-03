package datamaintain.core

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.Tag
import datamaintain.core.step.executor.ExecutionMode
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
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
            get { doesCreateTagsFromFolder }.isTrue()
            get { executionMode }.isEqualTo(ExecutionMode.DRY)
            get { verbose }.isTrue()
        }
    }

    @Test
    fun `should contain default values`() {
        val config = DatamaintainConfig.buildConfig(DatamaintainConfigTest::class.java.getResourceAsStream("/config/minimal.properties"),
                FakeDriverConfig())
        expectThat(config) and {
            get { identifierRegex.pattern }.isEqualTo(CoreConfigKey.SCAN_IDENTIFIER_REGEX.default)
            get { doesCreateTagsFromFolder }.isEqualTo(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.default!!.toBoolean())
            get { executionMode }.isEqualTo(ExecutionMode.NORMAL)
            get { verbose }.isFalse()
        }
    }

    @Test
    fun `should be overridden by jvm`() {
        System.setProperty("scan.path", "/new")
        System.setProperty(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.key, "false")
        System.setProperty(CoreConfigKey.EXECUTION_MODE.key, "NORMAL")
        System.setProperty(CoreConfigKey.VERBOSE.key, "FALSE")

        val config = DatamaintainConfig.buildConfig(DatamaintainConfigTest::class.java.getResourceAsStream("/config/default.properties"),
                FakeDriverConfig())
        expectThat(config).and {
            get { path }.isEqualTo(Paths.get("/new"))
            get { identifierRegex.pattern }.isEqualTo("(.*?)_.*")
            get { doesCreateTagsFromFolder }.isFalse()
            get { blacklistedTags }.isEqualTo(setOf(Tag("un"), Tag("deux")))
            get { executionMode }.isEqualTo(ExecutionMode.NORMAL)
            get { verbose }.isFalse()
        }
    }
}
