package datamaintain.core.db.driver

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.executor.Execution

abstract class DatamaintainDriver(protected val uri: String) {

    /**
     * Reads the executed scripts from the database and returns them
     */
    abstract fun listExecutedScripts(): Sequence<ExecutedScript>

    /**
     * Executes the given script and inserts its execution in the database
     */
    abstract fun executeScript(script: ScriptWithContent): Execution

    /**
     * Does not execute the given script, only inserts its execution in the database
     */
    abstract fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript

    /**
     * Does not execute the given script, only update its execution in the database
     */
    abstract fun overrideScript(executedScript: ExecutedScript): ExecutedScript
}
