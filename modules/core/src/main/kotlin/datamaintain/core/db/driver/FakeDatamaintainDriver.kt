package datamaintain.core.db.driver

import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.report.ExecutionLineReport


class FakeDatamaintainDriver: DatamaintainDriver {
    override fun executeScript(script: ScriptWithContent): ExecutionLineReport {
        throw NotImplementedError("FakeDatamaintainDriver executeScript method should not be used")
    }

    override fun listExecutedScripts(): Sequence<Script> {
        throw NotImplementedError("FakeDatamaintainDriver listExecutedScripts method should not be used")
    }

    override fun markAsExecuted(script: Script) {
        throw NotImplementedError("FakeDatamaintainDriver markAsExecuted method should not be used")
    }

}