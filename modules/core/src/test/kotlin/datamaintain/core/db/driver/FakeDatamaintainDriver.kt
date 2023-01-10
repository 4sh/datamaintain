package datamaintain.core.db.driver

import datamaintain.core.step.executor.Execution
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ScriptWithContent


class FakeDatamaintainDriver : DatamaintainDriver("", "") {
    override fun executeScript(script: ScriptWithContent): Execution {
        throw NotImplementedError("FakeDatamaintainDriver executeScript method should not be used")
    }

    override fun listExecutedScripts(): Sequence<ExecutedScript> {
        throw NotImplementedError("FakeDatamaintainDriver listExecutedScripts method should not be used")
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        throw NotImplementedError("FakeDatamaintainDriver markAsExecuted method should not be used")
    }

    override fun overrideScript(executedScript: ExecutedScript): ExecutedScript {
        throw NotImplementedError("FakeDatamaintainDriver override method should not be used")
    }
}