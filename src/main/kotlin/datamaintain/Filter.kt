package datamaintain

class Filter(private val config: Config) {
    fun filter(scripts: List<ScriptWithContent>): List<ScriptWithContent> {
        return scripts.filterNot { script -> config.blacklistedTags.any { it matchedBy script } }
    }
}
