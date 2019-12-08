package datamaintain.core.step

import datamaintain.core.Config
import datamaintain.core.script.Script

class Pruner(private val config: Config) {
    fun <T : Script> prune(scripts: List<T>): List<T> {
        val executedScripts: Sequence<Script> = config.dbDriver.listExecutedScripts()
        val executedChecksums = executedScripts.map { executedScript -> executedScript.checksum }
        return scripts.filterNot { executedChecksums.contains(it.checksum) }
    }
}