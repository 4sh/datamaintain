package datamaintain.core.script

import datamaintain.core.exception.DatamaintainBaseException
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
            return TagMatcher(Tag(name = name), pathMatchers.split(",")
                    .map { pathMatcher -> pathMatcher.trim('[', ']', ' ') }
                    .onEach { if (containsEnvironmentVariables(it)) { throw  DatamaintainPathMatcherUsesEnvironmentVariablesException(name, it)} }
                    .toSet())
        }

        @JvmStatic
        private fun containsEnvironmentVariables(pathMatcher: String): Boolean {
            return pathMatcher.startsWith("~") || pathMatcher.startsWith("\$HOME")
        }
    }
}

class DatamaintainPathMatcherUsesEnvironmentVariablesException(name: String, pathMatcher: String):
        DatamaintainBaseException("The path matcher '${pathMatcher}' given to match scripts for the tag named '${name}' is not valid " +
                "because it starts with a '~' or '\$HOME'. You can not use such environments variables in the settings.\n" +
                "You may want to give the absolute path to your folder. If that is what you want, you can find it using " +
                "the command `pwd` in the concerned folder.\n")
