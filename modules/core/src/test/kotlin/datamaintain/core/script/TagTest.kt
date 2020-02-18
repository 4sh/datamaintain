package datamaintain.core.script

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.size
import java.nio.file.Paths

internal class TagTest {
    @Nested
    inner class Match {
        @Test
        fun `tag should match another tag`() {
            Tag("test") matches Tag("test")
        }

        @Test
        fun `tag should not match another tag`() {
            Tag("test") matches Tag("test2")
        }

        @Test
        fun `tag should be matched by script`() {
            Tag("test") matchedBy FileScript(
                    Paths.get(""),
                    Regex(""),
                    setOf(Tag("test"), Tag("test2"))
            )
        }

        @Test
        fun `tag should not be matched by script`() {
            Tag("test") matchedBy FileScript(
                    Paths.get(""),
                    Regex(""),
                    setOf(Tag("test3"), Tag("test2"))
            )
        }
    }

    @Nested
    inner class Parse {
        @Test
        fun `should parse from correct string`() {
            // Given
            val pathMatcher1 = "src/test/resources/scanner_test_files/01_file1"
            val pathMatcher2 = "src/test/resources/scanner_test_files/subfolder/*"
            val pathMatchersString = "[$pathMatcher1,$pathMatcher2]"
            val tagName = "TOTO"

            // When
            val tag = Tag.parse(tagName, pathMatchersString)

            // Then
            expectThat(tag).and {
                get { name }.isEqualTo(tagName)
                get { pathMatchers }.and {
                    size.isEqualTo(2)
                }
            }
        }
    }
}