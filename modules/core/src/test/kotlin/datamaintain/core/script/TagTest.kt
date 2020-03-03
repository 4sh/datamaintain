package datamaintain.core.script

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class TagTest {
    @Nested
    inner class Match {
        @Test
        fun `tag should match another tag`() {
            Tag("test") == Tag("test")
        }

        @Test
        fun `tag should not match another tag`() {
            Tag("test") == Tag("test2")
        }

        @Test
        fun `tag should be matched by script`() {
            Tag("test") isIncluded FileScript(
                    Paths.get(""),
                    Regex(""),
                    setOf(Tag("test"), Tag("test2"))
            )
        }

        @Test
        fun `tag should not be matched by script`() {
            Tag("test") isIncluded FileScript(
                    Paths.get(""),
                    Regex(""),
                    setOf(Tag("test3"), Tag("test2"))
            )
        }
    }
}