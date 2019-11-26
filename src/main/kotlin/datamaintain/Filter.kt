package datamaintain

class Filter(private val config: Config) {
    fun <T : Script> filter(scripts: List<T>): List<T> {
        return scripts.filterNot {script -> config isScriptBlacklisted script }
    }
}
