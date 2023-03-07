package datamaintain.test

import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.step.executor.Execution
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ScriptWithContent

/**
 * Duplicated, needs refactoring
 * TODO: https://github.com/4sh/datamaintain/issues/213
 */
class FakeDatamaintainDriver : DatamaintainDriver("") {
    override fun executeScript(script: ScriptWithContent): Execution {
        throw NotImplementedError("FakeDatamaintainDriver executeScript method should not be used")
    }

    override fun listExecutedScripts(): Sequence<ExecutedScript> = sequenceOf()

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        throw NotImplementedError("FakeDatamaintainDriver markAsExecuted method should not be used")
    }

    override fun overrideScript(executedScript: ExecutedScript): ExecutedScript {
        throw NotImplementedError("FakeDatamaintainDriver override method should not be used")
    }
}