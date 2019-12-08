package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.ScriptWithContent

class Filter(private val context: Context) {
    fun filter(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        return scripts.filterNot { script -> context.config.blacklistedTags.any { it matchedBy script } }
    }
}
