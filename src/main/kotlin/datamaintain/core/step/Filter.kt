package datamaintain.core.step

import datamaintain.core.Config
import datamaintain.core.script.ScriptWithContent

class Filter(private val config: Config) {
    fun filter(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        return scripts.filterNot { script -> config.blacklistedTags.any { it matchedBy script } }
    }
}
