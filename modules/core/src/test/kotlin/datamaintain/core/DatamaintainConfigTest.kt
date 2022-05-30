package datamaintain.core

import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.exception.DatamaintainBuilderMandatoryException
import datamaintain.core.script.ScriptAction
import datamaintain.core.script.Tag
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.executor.ExecutionMode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*
import java.nio.file.Paths
import java.util.*

class DatamaintainConfigTest {

    @Test
    fun `should build config from resource`() {
        val config = DatamaintainConfig.buildConfig(DatamaintainConfigTest::class.java.getResourceAsStream("/config/default.properties"),
                FakeDriverConfig())

        assertMyDefaultConfig(config)
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
            get { porcelain }.isFalse()
            get { name }.isNull()
        }
    }

    @Test
    fun `should be overridden by jvm`() {
        System.setProperty("scan.path", "/new")
        System.setProperty(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.key, "false")
        System.setProperty(CoreConfigKey.EXECUTION_MODE.key, "NORMAL")
        System.setProperty(CoreConfigKey.VERBOSE.key, "FALSE")
        System.setProperty(CoreConfigKey.PRINT_RELATIVE_PATH_OF_SCRIPT.key, "false")

        val config = DatamaintainConfig.buildConfig(DatamaintainConfigTest::class.java.getResourceAsStream("/config/default.properties"),
                FakeDriverConfig())
        expectThat(config).and {
            get { path }.isEqualTo(Paths.get("/new"))
            get { identifierRegex.pattern }.isEqualTo("(.*?)_.*")
            get { doesCreateTagsFromFolder }.isFalse()
            get { blacklistedTags }.isEqualTo(setOf(Tag("un"), Tag("deux")))
            get { tagsToPlayAgain }.isEqualTo(setOf(Tag("again")))
            get { executionMode }.isEqualTo(ExecutionMode.NORMAL)
            get { verbose }.isFalse()
            get { porcelain }.isFalse()
        }

        System.clearProperty("scan.path")
        System.clearProperty(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.key)
        System.clearProperty(CoreConfigKey.EXECUTION_MODE.key)
        System.clearProperty(CoreConfigKey.VERBOSE.key)
        System.clearProperty(CoreConfigKey.PRINT_RELATIVE_PATH_OF_SCRIPT.key)
    }

    @Test
    fun `should construct scan path from user_dir`() {
        val properties = Properties()
        properties.setProperty(CoreConfigKey.SCAN_PATH.key, "./scanPath")

        val config = DatamaintainConfig.buildConfig(FakeDriverConfig(), properties)

        expectThat(config).and {
            get { path }.isEqualTo(Paths.get(System.getProperty("user.dir"),"scanPath"))
        }
    }

    @Test
    fun `should not alter scan path because absolute`() {
        val properties = Properties()
        properties.setProperty(CoreConfigKey.WORKING_DIRECTORY_PATH.key, "/tmp")
        properties.setProperty(CoreConfigKey.SCAN_PATH.key, "/var/scanPath")

        val config = DatamaintainConfig.buildConfig(FakeDriverConfig(), properties)

        expectThat(config).and {
            get { path }.isEqualTo(Paths.get("/var/scanPath"))
        }
    }

    @Test
    fun `should construct scan path from working directory`() {
        val properties = Properties()
        properties.setProperty(CoreConfigKey.WORKING_DIRECTORY_PATH.key, "/tmp")
        properties.setProperty(CoreConfigKey.SCAN_PATH.key, "./scanPath")

        val config = DatamaintainConfig.buildConfig(FakeDriverConfig(), properties)

        expectThat(config).and {
            get { path }.isEqualTo(Paths.get("/tmp/scanPath"))
        }
    }

    @Test
    fun `should load minimal properties as parent config`() {
        val configFilePath = DatamaintainConfigTest::class.java.getResource("/config/default.properties")?.file

        val props = Properties()
        props.put(CoreConfigKey.PARENT_CONFIG_PATH.key, configFilePath)

        val config = DatamaintainConfig.buildConfig(FakeDriverConfig(), props)

        assertMyDefaultConfig(config)
    }

    private fun assertMyDefaultConfig(config: DatamaintainConfig) {
        val expectedPath = Paths.get("/tmp/test")

        expectThat(config).and {
            get { path }.isEqualTo(expectedPath)
            get { identifierRegex.pattern }.isEqualTo("(.*?)_.*")
            get { whitelistedTags }.isEqualTo(setOf(Tag("trois"), Tag("quatre")))
            get { blacklistedTags }.isEqualTo(setOf(Tag("un"), Tag("deux")))
            get { tagsToPlayAgain }.isEqualTo(setOf(Tag("again")))
            get { doesCreateTagsFromFolder }.isTrue()
            get { executionMode }.isEqualTo(ExecutionMode.DRY)
            get { tagsMatchers }.containsExactlyInAnyOrder(
                    TagMatcher(Tag("TOTO"), setOf(
                            expectedPath.resolve(Paths.get("src/test/resources/scanner_test_files/01_file1")).toString(),
                            expectedPath.resolve(Paths.get("src/test/resources/scanner_test_files/subfolder/*")).toString()
                    )),
                    TagMatcher(Tag("potato"), setOf(
                            expectedPath.resolve(Paths.get("src/test/resources/scanner_test_files/*")).toString(),
                            expectedPath.resolve(Paths.get("src/test/resources/scanner_test_files/subfolder/03_file3")).toString()
                    ))
            )
            get { verbose }.isTrue()
            get { porcelain }.isTrue()
            get { name }.isEqualTo("myDefaultConfig")
        }
    }

    @Nested
    inner class BuilderTest {
        @Test
        fun `should build config with builder`() {
            val config = DatamaintainConfig.Builder()
                .withDriverConfig(FakeDriverConfig())
                .withName("a name")
                .withWorkingDirectory(Paths.get("/working"))
                .withPath(Paths.get("/path"))
                .withIdentifierRegex(".*".toRegex())
                .withDoesCreateTagsFromFolder(true)
                .withOverrideExecutedScripts(true)
                .withExecutionMode(ExecutionMode.DRY)
                .withDefaultScriptAction(ScriptAction.OVERRIDE_EXECUTED)
                .withVerbose(true)
                .withPorcelain(true)
                .addWhitelistedTag(Tag("whitelisted1"))
                .addWhitelistedTag(Tag("whitelisted2"))
                .addBlacklistedTag(Tag("blacklisted1"))
                .addBlacklistedTag(Tag("blacklisted2"))
                .addTagToPlayAgain(Tag("tagToPlayAgain1"))
                .addTagToPlayAgain(Tag("tagToPlayAgain2"))
                .addTagMatcher(TagMatcher(Tag("1"), listOf(".*")))
                .addTagMatcher(TagMatcher(Tag("2"), listOf(".*")))
                .addCheckRule("checkRules")
                .addFlag("1")
                .addFlag("2")
                .build()

            expectThat(config).and {
                get { name } isEqualTo "a name"
                get { workingDirectory } isEqualTo Paths.get("/working")
                get { path } isEqualTo Paths.get("/path")
                get { identifierRegex.pattern } isEqualTo ".*"
                get { doesCreateTagsFromFolder }.isTrue()
                get { overrideExecutedScripts }.isTrue()
                get { executionMode } isEqualTo ExecutionMode.DRY
                get { defaultScriptAction } isEqualTo ScriptAction.OVERRIDE_EXECUTED
                get { verbose }.isTrue()
                get { porcelain }.isTrue()
                get { whitelistedTags }.containsExactlyInAnyOrder(Tag("whitelisted1"), Tag("whitelisted2"))
                get { blacklistedTags }.containsExactlyInAnyOrder(Tag("blacklisted1"), Tag("blacklisted2"))
                get { tagsToPlayAgain }.containsExactlyInAnyOrder(Tag("tagToPlayAgain1"), Tag("tagToPlayAgain2"))
                get { tagsMatchers }.containsExactlyInAnyOrder(
                    TagMatcher(Tag("1"), listOf(".*")),
                    TagMatcher(Tag("2"), listOf(".*"))
                )
                get { checkRules.toList() }.containsExactlyInAnyOrder("checkRules")
                get { flags }.containsExactlyInAnyOrder("1", "2")
            }
        }

        @Test
        fun `should build with default config`() {
            val config = DatamaintainConfig.Builder()
                .withDriverConfig(FakeDriverConfig())
                .build()

            expectThat(config).and {
                get { name }.isNull()
                get { workingDirectory } isEqualTo Paths.get(System.getProperty("user.dir"))
                get { path } isEqualTo Paths.get(CoreConfigKey.SCAN_PATH.default!!)
                get { identifierRegex.pattern } isEqualTo CoreConfigKey.SCAN_IDENTIFIER_REGEX.default!!
                get { doesCreateTagsFromFolder } isEqualTo CoreConfigKey.CREATE_TAGS_FROM_FOLDER.default!!.toBoolean()
                get { overrideExecutedScripts } isEqualTo CoreConfigKey.PRUNE_OVERRIDE_UPDATED_SCRIPTS.default!!.toBoolean()
                get { executionMode } isEqualTo ExecutionMode.NORMAL
                get { defaultScriptAction } isEqualTo ScriptAction.RUN
                get { verbose }.isFalse()
                get { porcelain }.isFalse()
                get { whitelistedTags }.isEmpty()
                get { blacklistedTags }.isEmpty()
                get { tagsToPlayAgain }.isEmpty()
                get { tagsMatchers }.isEmpty()
                get { checkRules.toList() }.isEmpty()
                get { flags }.isEmpty()
            }
        }

        @Test
        fun `should raise error because driverConfig is not set in builder`() {
            expectThrows<DatamaintainBuilderMandatoryException>{ DatamaintainConfig.Builder().build() }
                .get { message } isEqualTo "Cannot build DatamaintainConfigBuilder : driverConfig is mandatory"
        }
    }
}
