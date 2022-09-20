package datamaintain.core.db.driver

import datamaintain.core.step.executor.Execution
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.LightExecutedScript
import datamaintain.domain.script.ScriptWithContent

abstract class DatamaintainDriver(protected val uri: String) {

    /**
     * Reads the executed scripts from the database and returns them
     */
    abstract fun listExecutedScripts(): Sequence<LightExecutedScript>

    /**
     * Executes the given script
     */
    abstract fun executeScript(script: ScriptWithContent): Execution

    /**
     * Inserts script execution in the database
     */
    abstract fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript

    /**
     * Update script execution in the database
     */
    abstract fun overrideScript(executedScript: ExecutedScript): ExecutedScript
}
