package datamaintain

import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class TagTest {
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