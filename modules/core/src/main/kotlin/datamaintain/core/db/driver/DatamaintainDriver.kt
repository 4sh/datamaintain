package datamaintain.core.db.driver

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.executor.Execution

interface DatamaintainDriver {

    /**
     * Reads the executed scripts from the database and returns them
     */
    fun listExecutedScripts(): Sequence<ExecutedScript>

    /**
     * Executes the given script and inserts its execution in the database
     */
    fun executeScript(script: ScriptWithContent): Execution

    /**
     * Does not execute the given script, only inserts its execution in the database
     */
    fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript
}
