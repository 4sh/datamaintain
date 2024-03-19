package datamaintain.core.script

import datamaintain.domain.script.Tag
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class TagMatcherTest {
    @Nested
    inner class Parse {
        @Test
        fun `should parse from correct glob paths`() {
            // Given
            val scanPathString = "src/test/resources/scanner_test_files"
            val scanPath = Paths.get(scanPathString).toAbsolutePath()
            val pathMatcher1 = "${scanPath}/01_file1"
            val pathMatcher2 = "${scanPath}/subfolder/*"
            val pathMatchersString = "[$pathMatcher1,$pathMatcher2]"
            val tagName = "TOTO"
            val expectedTag = Tag(tagName)

            // When
            val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

            // Then
            expectThat(tagMatcher).and {
                get { tag }.isEqualTo(expectedTag)
                get { globPaths }.containsExactly(pathMatcher1, pathMatcher2)
            }
        }

        @Test
        fun `should parse from correct glob paths without bracket`() {
            // Given
            val scanPathString = "src/test/resources/scanner_test_files"
            val scanPath = Paths.get(scanPathString).toAbsolutePath()
            val pathMatcher1 = "${scanPath}/01_file1"
            val pathMatcher2 = "${scanPath}/subfolder/*"
            val pathMatchersString = "$pathMatcher1,$pathMatcher2"
            val tagName = "TOTO"
            val expectedTag = Tag(tagName)

            // When
            val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

            // Then
            expectThat(tagMatcher).and {
                get { tag }.isEqualTo(expectedTag)
                get { globPaths }.containsExactly(pathMatcher1, pathMatcher2)
            }
        }

        @Test
        fun `should parse from correct glob paths with space after comma`() {
            // Given
            val scanPathString = "src/test/resources/scanner_test_files"
            val scanPath = Paths.get(scanPathString).toAbsolutePath()
            val pathMatcher1 = "${scanPath}/01_file1"
            val pathMatcher2 = "${scanPath}/subfolder/*"
            val pathMatchersString = "[$pathMatcher1, $pathMatcher2]"
            val tagName = "TOTO"
            val expectedTag = Tag(tagName)

            // When
            val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

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
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]", Paths.get(".")) }
                        .failed()
                        .isA<DatamaintainPathMatcherUsesEnvironmentVariablesException>()
            }

            @Test
            fun `should throw an exception when path matcher starts with $HOME`() {
                // Given
                val pathMatcher = "\$HOME/me/path"
                val tagName = "tag"

                // When
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]", Paths.get(".")) }
                        .failed()
                        .isA<DatamaintainPathMatcherUsesEnvironmentVariablesException>()
            }

            @Test
            fun `should not throw an exception when path matcher contains relative path`() {
                // Given
                val pathMatcher = "/me/../path"
                val tagName = "tag"

                // When
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]", Paths.get(".")) }
                        .succeeded()
            }

            @Test
            fun `should not throw an exception when path matcher starts with dot`() {
                // Given
                val pathMatcher = "./path"
                val tagName = "tag"

                // When
                expectCatching { TagMatcher.parse(tagName, "[$pathMatcher]", Paths.get(".")) }
                    .succeeded()
            }
        }

        @Nested
        inner class ParsePath {
            @Test
            fun `should parse relative path`() {
                // Given
                val scanPathString = "src/test/resources/scanner_test_files"
                val scanRelativePath = Paths.get(scanPathString)
                val scanFullPath = scanRelativePath.toAbsolutePath()

                val pathToFile = "01_file1"
                val pathMatchersString = "[$pathToFile]"
                val tagName = "TOTO"

                // When
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanRelativePath)

                // Then
                expectThat(tagMatcher)
                    .get { globPaths }
                    .and {
                        assertThat("size") { it.count() == 1 }
                        get { first() }
                            .isEqualTo("$scanFullPath/$pathToFile")
                    }
            }

            @Test
            fun `should parse relative path with dot`() {
                // Given
                val scanRelativePathString = "src/test/resources/scanner_test_files"
                val scanRelativePathWithDotString = "./$scanRelativePathString"
                val scanPathWithDot = Paths.get(scanRelativePathWithDotString)
                val scanPathFullPath = Paths.get(scanRelativePathString).toAbsolutePath()

                val pathToFile = "01_file1"
                val pathMatchersString = "[$pathToFile]"
                val tagName = "TOTO"

                // When
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPathWithDot)

                // Then
                expectThat(tagMatcher)
                    .get { globPaths }
                    .and {
                        assertThat("size") { it.count() == 1 }
                        get { first() }
                            .isEqualTo("$scanPathFullPath/$pathToFile")
                    }
            }

            @Test
            fun `should parse relative path with globs`() {
                // Given
                val scanPathString = "src/test/resources/scanner_test_files"
                val scanRelativePath = Paths.get(scanPathString)
                val scanFullPath = scanRelativePath.toAbsolutePath()

                val glob = "*file1"
                val pathMatchersString = "[$glob]"
                val tagName = "TOTO"

                // When
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanRelativePath)

                // Then
                expectThat(tagMatcher)
                    .get { globPaths }
                    .and {
                        assertThat("size") { it.count() == 1 }
                        get { first() }
                            .isEqualTo("$scanFullPath/$glob")
                    }
            }

            @Test
            fun `should parse full path`() {
                // Given
                val scanPathString = "src/test/resources/scanner_test_files"
                val scanPath = Paths.get(scanPathString).toAbsolutePath()

                val pathToFile = "$scanPath/01_file1"
                val pathMatchersString = "[$pathToFile]"
                val tagName = "TOTO"

                // When
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

                // Then
                expectThat(tagMatcher)
                    .get { globPaths }
                    .and {
                        assertThat("size") { it.count() == 1 }
                        get { first() }
                            .isEqualTo(pathToFile)
                    }
            }

            @Test
            fun `should parse full path with globs`() {
                // Given
                val scanPathString = "src/test/resources/scanner_test_files"
                val scanPath = Paths.get(scanPathString).toAbsolutePath()

                val glob = "$scanPath/*file1"
                val pathMatchersString = "[$glob]"
                val tagName = "TOTO"

                // When
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

                // Then
                expectThat(tagMatcher)
                    .get { globPaths }
                    .and {
                        assertThat("size") { it.count() == 1 }
                        get { first() }
                            .isEqualTo(glob)
                    }
            }

            @Test
            fun `should parse full path with dot`() {
                // Given
                val scanPathString = "src/test/resources/scanner_test_files"
                val scanPathWithDot = Paths.get("./$scanPathString").toAbsolutePath()
                val scanPath = Paths.get(scanPathString).toAbsolutePath()

                val pathToFile = "$scanPath/01_file1"
                val pathMatchersString = "[$pathToFile]"
                val tagName = "TOTO"

                // When
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPathWithDot)

                // Then
                expectThat(tagMatcher)
                    .get { globPaths }
                    .and {
                        assertThat("size") { it.count() == 1 }
                        get { first() }
                            .isEqualTo(pathToFile)
                    }
            }
        }

        @Nested
        inner class Match {
            @Test
            fun `should match`() {
                // Given
                var scanPathString = "src/test/resources/scanner_test_files"
                val scanPath = Paths.get(scanPathString).toAbsolutePath()
                scanPathString = scanPath.toString()

                val pathToFile = "$scanPathString/01_file1"
                val pathMatchersString = "[$pathToFile]"

                val tagName = "TOTO"
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

                // When
                val tagMatch = tagMatcher.matches(Paths.get(pathToFile))

                // Then
                expectThat(tagMatch).isTrue()
            }

            @Test
            fun `should match with glob`() {
                // Given
                var scanPathString = "src/test/resources/scanner_test_files"
                val scanPath = Paths.get(scanPathString).toAbsolutePath()
                scanPathString = scanPath.toString()

                val pathToFile = "$scanPathString/01_file1"
                val pathMatchersString = "[$scanPathString/*file1]"

                val tagName = "TOTO"
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

                // When
                val tagMatch = tagMatcher.matches(Paths.get(pathToFile))

                // Then
                expectThat(tagMatch).isTrue()
            }

            @Test
            fun `should not match`() {
                // Given
                var scanPathString = "src/test/resources/scanner_test_files"
                val scanPath = Paths.get(scanPathString).toAbsolutePath()
                scanPathString = scanPath.toString()

                val pathMatcher = "$scanPathString/01_file1"
                val pathToFile = "$scanPathString/01_file2"
                val pathMatchersString = "[$pathMatcher]"
                val tagName = "TOTO"
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

                // When
                val tagMatch = tagMatcher.matches(Paths.get(pathToFile))

                // Then
                expectThat(tagMatch).isFalse()
            }

            @Test
            fun `should not match with glob`() {
                // Given
                var scanPathString = "src/test/resources/scanner_test_files"
                val scanPath = Paths.get(scanPathString).toAbsolutePath()
                scanPathString = scanPath.toString()

                val pathMatcher = "$scanPathString/01_file1"
                val pathMatchersString = "[$scanPathString/*file2]"
                val tagName = "TOTO"
                val tagMatcher = TagMatcher.parse(tagName, pathMatchersString, scanPath)

                // When
                val tagMatch = tagMatcher.matches(Paths.get(pathMatcher))

                // Then
                expectThat(tagMatch).isFalse()
            }
        }
    }
}
