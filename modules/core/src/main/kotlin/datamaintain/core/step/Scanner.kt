package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent

class Scanner(private val context: Context) {
    fun scan(): List<ScriptWithContent> = context.config.path.toFile().walk()
            .filter { it.isFile }
            .map {it.toPath()}
            .map {
                FileScript(it, context.config.identifierRegex,
                        context.config.tags.filter { tag -> tag.matchesPath(it) }.toSet())
            }
            .sortedBy { it.name }.toList()
}