package datamaintain.core.step

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.Context
import datamaintain.core.db.driver.FakeDatamaintainDriver
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.Tag
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.size
import java.nio.file.Paths

internal class ScannerTest {
    private val scanner = Scanner(Context(
            DatamaintainConfig(Paths.get("src/test/resources/scanner_test_files"),
                    Regex("(.*?)_.*"), driverConfig = FakeDriverConfig()),
            dbDriver = FakeDatamaintainDriver()))

    @Test
    fun `should collect script names`() {
        // Given

        // When
        val scripts = scanner.scan()

        // Then
        expectThat(scripts) {
            size.isEqualTo(6)
            get(0).get { this.name }.isEqualTo("01_file1")
            get(1).get { this.name }.isEqualTo("02_file2")
            get(2).get { this.name }.isEqualTo("03_file3")
            get(3).get { this.name }.isEqualTo("04_file4")
            get(4).get { this.name }.isEqualTo("10_file10")
            get(5).get { this.name }.isEqualTo("11_file11")
        }
    }

    @Test
    fun `should collect script contents`() {
        // Given

        // When
        val scripts = scanner.scan()

        // Then
        expectThat(scripts) {
            size.isEqualTo(6)
            get(0).get { this.content }.isEqualTo("1")
            get(1).get { this.content }.isEqualTo("2")
            get(2).get { this.content }.isEqualTo("3")
            get(3).get { this.content }.isEqualTo("4")
            get(4).get { this.content }.isEqualTo("10")
            get(5).get { this.content }.isEqualTo("11")
        }
    }

    @Test
    fun `should collect script checksums`() {
        // Given

        // When
        val scripts = scanner.scan()

        // Then
        expectThat(scripts) {
            size.isEqualTo(6)
            get(0).get { this.checksum }.isEqualTo("c4ca4238a0b923820dcc509a6f75849b")
            get(1).get { this.checksum }.isEqualTo("c81e728d9d4c2f636f067f89cc14862c")
            get(2).get { this.checksum }.isEqualTo("eccbc87e4b5ce2fe28308fd9f2a7baf3")
            get(3).get { this.checksum }.isEqualTo("a87ff679a2f3e71d9181a67b7542122c")
            get(4).get { this.checksum }.isEqualTo("d3d9446802a44259755d38e6d163e820")
            get(5).get { this.checksum }.isEqualTo("6512bd43d9caa6e02c990b0a82652dca")
        }
    }

    @Test
    fun `should collect script identifiers`() {
        // Given

        // When
        val scripts = scanner.scan()

        // Then
        expectThat(scripts) {
            size.isEqualTo(6)
            get(0).get { this.identifier }.isEqualTo("01")
            get(1).get { this.identifier }.isEqualTo("02")
            get(2).get { this.identifier }.isEqualTo("03")
            get(3).get { this.identifier }.isEqualTo("04")
            get(4).get { this.identifier }.isEqualTo("10")
            get(5).get { this.identifier }.isEqualTo("11")
        }
    }

    @Test
    fun `should create tags from parent`() {
        // Given
        val scanner = Scanner(Context(
                DatamaintainConfig(
                        Paths.get("src/test/resources/scanner_test_files"),
                        Regex("(.*?)_.*"),
                        driverConfig = FakeDriverConfig(),
                        doesCreateTagsFromFolder = true),
                dbDriver = FakeDatamaintainDriver()))

        // When
        val scripts = scanner.scan()

        // Then
        expectThat(scripts) {
            size.isEqualTo(6)
            get(0).get { this.name }.isEqualTo("01_file1")
            get(0).get { this.tags }.isEmpty()
            get(1).get { this.name }.isEqualTo("02_file2")
            get(1).get { this.tags }.isEmpty()
            get(2).get { this.name }.isEqualTo("03_file3")
            get(2).get { this.tags }.isEqualTo(setOf(Tag("subfolder")))
            get(3).get { this.name }.isEqualTo("04_file4")
            get(3).get { this.tags }.isEqualTo(setOf(Tag("subfolder")))
            get(4).get { this.name }.isEqualTo("10_file10")
            get(4).get { this.tags }.isEmpty()
            get(5).get { this.name }.isEqualTo("11_file11")
            get(5).get { this.tags }.isEqualTo(setOf(Tag("subfolder"), Tag("old")))
        }
    }
}
