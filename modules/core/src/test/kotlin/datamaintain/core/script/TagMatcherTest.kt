package datamaintain.core.script

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

internal class TagMatcherTest {
    @Nested
    inner class Parse {
        @Test
        fun `should parse from correct glob paths`() {
            // Given
            val pathMatcher1 = "src/test/resources/scanner_test_files/01_file1"
            val pathMatcher2 = "src/test/resources/scanner_test_files/subfolder/*"
            val pathMatchersString = "[$pathMatcher1,$pathMatcher2]"
            val tagName = "TOTO"
            val expectedTag = Tag(tagName)

            // When
            val tagMatcher = TagMatcher.parse(tagName, pathMatchersString)

            // Then
            expectThat(tagMatcher).and {
                get { tag }.isEqualTo(expectedTag)
                get { globPaths }.containsExactly(pathMatcher1, pathMatcher2)
            }
        }

        @Test
        fun `should parse from correct glob paths without bracket`() {
            // Given
            val pathMatcher1 = "src/test/resources/scanner_test_files/01_file1"
            val pathMatcher2 = "src/test/resources/scanner_test_files/subfolder/*"
            val pathMatchersString = "$pathMatcher1,$pathMatcher2"
            val tagName = "TOTO"
            val expectedTag = Tag(tagName)

            // When
            val tagMatcher = TagMatcher.parse(tagName, pathMatchersString)

            // Then
            expectThat(tagMatcher).and {
                get { tag }.isEqualTo(expectedTag)
                get { globPaths }.containsExactly(pathMatcher1, pathMatcher2)
            }
        }

        @Test
        fun `should parse from correct glob paths with space after comma`() {
            // Given
            val pathMatcher1 = "src/test/resources/scanner_test_files/01_file1"
            val pathMatcher2 = "src/test/resources/scanner_test_files/subfolder/*"
            val pathMatchersString = "[$pathMatcher1, $pathMatcher2]"
            val tagName = "TOTO"
            val expectedTag = Tag(tagName)

            // When
            val tagMatcher = TagMatcher.parse(tagName, pathMatchersString)

            // Then
            expectThat(tagMatcher).and {
                get { tag }.isEqualTo(expectedTag)
                get { globPaths }.containsExactly(pathMatcher1, pathMatcher2)
            }
        }
    }
}