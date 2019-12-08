package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.Script

class Pruner(private val context: Context) {
    fun <T : Script> prune(scripts: List<T>): List<T> {
        val executedScripts: Sequence<Script> = context.dbDriver.listExecutedScripts()
        val executedChecksums = executedScripts.map { executedScript -> executedScript.checksum }
        return scripts.filterNot { executedChecksums.contains(it.checksum) }
    }
}