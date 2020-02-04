package datamaintain.core.db.driver

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent


class FakeDatamaintainDriver : DatamaintainDriver {
    override fun executeScript(script: ScriptWithContent): ExecutedScript {
        throw NotImplementedError("FakeDatamaintainDriver executeScript method should not be used")
    }

    override fun listExecutedScripts(): Sequence<Script> {
        throw NotImplementedError("FakeDatamaintainDriver listExecutedScripts method should not be used")
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        throw NotImplementedError("FakeDatamaintainDriver markAsExecuted method should not be used")
    }

}