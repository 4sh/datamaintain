package datamaintain

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.isEqualTo
import java.nio.file.Paths

internal class ScannerTest {
    private val scanner = Scanner(Config(Paths.get("src/test/resources/scanner_test_files"),
            "",
            ""))

    @Test
    fun `should collect script names`() {
        // Given

        // When
        val scripts = scanner.scan()

        // Then
        expectThat(scripts) {
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
            get(0).get { this.checksum }.isEqualTo("c4ca4238a0b923820dcc509a6f75849b")
            get(1).get { this.checksum }.isEqualTo("c81e728d9d4c2f636f067f89cc14862c")
            get(2).get { this.checksum }.isEqualTo("eccbc87e4b5ce2fe28308fd9f2a7baf3")
            get(3).get { this.checksum }.isEqualTo("a87ff679a2f3e71d9181a67b7542122c")
            get(4).get { this.checksum }.isEqualTo("d3d9446802a44259755d38e6d163e820")
            get(5).get { this.checksum }.isEqualTo("6512bd43d9caa6e02c990b0a82652dca")
        }
    }
}