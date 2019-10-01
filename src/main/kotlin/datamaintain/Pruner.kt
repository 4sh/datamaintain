package datamaintain

class Pruner(private val config: Config) {
    fun <T : Script> prune(scripts: List<T>): List<T> {
        val executedScripts: List<Script> = config.dbDriver?.listExecutedScripts() ?: listOf()
        val executedChecksums = executedScripts.map { executedScript -> executedScript.checksum }
        return scripts.filterNot { executedChecksums.contains(it.checksum) }
    }
}