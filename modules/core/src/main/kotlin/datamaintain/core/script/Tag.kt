package datamaintain.core.script

import java.nio.file.Path
import java.nio.file.PathMatcher

data class Tag(
        val name: String,
        val pathMatchers: Set<PathMatcher> = emptySet()
) {
    infix fun matchedBy(script: ScriptWithContent): Boolean {
        return script.tags.any { it matches this }
    }

    infix fun matches(tag: Tag): Boolean {
        return name == tag.name
    }

    infix fun matchesPath(path: Path): Boolean {
        return pathMatchers.any { pathMatcher -> pathMatcher.matches(path) }
    }
}