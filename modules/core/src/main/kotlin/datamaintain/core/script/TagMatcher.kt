package datamaintain.core.script

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher

class TagMatcher(val tag: Tag, val globPaths: Iterable<String>) {
    private val pathMatchers: Set<PathMatcher>

    init {
        pathMatchers = globPaths.map { globPath -> FileSystems.getDefault().getPathMatcher("glob:$globPath") }.toSet()
    }

    fun matches(path: Path): Boolean {
        return pathMatchers.any { pathMatcher -> pathMatcher.matches(path) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagMatcher

        if (tag != other.tag) return false
        if (globPaths != other.globPaths) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + globPaths.hashCode()
        result = 31 * result + pathMatchers.hashCode()
        return result
    }


    companion object {
        @JvmStatic
        fun parse(name: String, pathMatchers: String): TagMatcher {
            return TagMatcher(Tag(name = name),
                    pathMatchers.split(",")
                            .map { pathMatcher -> pathMatcher.trim('[', ']', ' ') }
                            .toSet())
        }
    }
}