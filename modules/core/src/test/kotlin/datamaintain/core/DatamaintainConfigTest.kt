package datamaintain.core

import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.Tag
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import strikt.assertions.map
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
            get { tags }.map { it.name }.containsExactly("TOTO", "potato")
//                    setOf(Tag("TOTO", setOf(
//                            FileSystems.getDefault().getPathMatcher("glob:src/test/resources/scanner_test_files/01_file1"),
//                            FileSystems.getDefault().getPathMatcher("glob:src/test/resources/scanner_test_files/subfolder/*")
//                    )), Tag("potato", setOf(
//                            FileSystems.getDefault().getPathMatcher("glob:src/test/resources/scanner_test_files/*"),
//                            FileSystems.getDefault().getPathMatcher("glob:src/test/resources/scanner_test_files/subfolder/03_file3")
//                    ))))
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
