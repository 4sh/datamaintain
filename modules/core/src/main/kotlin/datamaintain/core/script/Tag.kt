package datamaintain.core.script

import java.nio.file.FileSystems
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

    companion object {
        @JvmStatic
        fun parse(name: String, pathMatchers: String): Tag {
            return Tag(
                    name = name,
                    pathMatchers = pathMatchers.split(",")
                            .map { pathMatcher -> pathMatcher.trim('[', ']', ' ') }
                            .map { pathMatcher -> FileSystems.getDefault().getPathMatcher("glob:$pathMatcher") }.toSet())
        }
    }
}