package datamaintain.core.script

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

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

        @Nested
        inner class NotAbsolutePathMatcher {
            @Test
            fun `should throw an exception when path matcher starts with a tilde`() {
                // Given
                val pathMatcher = "~/me/path"
                val tagName = "tag"

                // When
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]") }
                        .failed()
                        .isA<DatamaintainPathMatcherUsesEnvironmentVariablesException>()
            }

            @Test
            fun `should throw an exception when path matcher starts with $HOME`() {
                // Given
                val pathMatcher = "\$HOME/me/path"
                val tagName = "tag"

                // When
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]") }
                        .failed()
                        .isA<DatamaintainPathMatcherUsesEnvironmentVariablesException>()
            }

            @Test
            fun `should not throw an exception when path matcher contains relative path`() {
                // Given
                val pathMatcher = "/me/../path"
                val tagName = "tag"

                // When
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]") }
                        .succeeded()
            }

            @Test
            fun `should not throw an exception when path matcher starts with dot`() {
                // Given
                val pathMatcher = "./path"
                val tagName = "tag"

                // When
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]") }
                        .succeeded()
            }
        }
    }
}
