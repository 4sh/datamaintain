package datamaintain.core.step

import datamaintain.core.Config
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent

class Scanner(private val config: Config) {
    fun scan(): List<ScriptWithContent> = config.path.toFile().walk()
                .filter { it.isFile }
                .map {
                    FileScript(it.toPath(), config.identifierRegex)
                }
                .sortedBy { it.name }.toList()
}