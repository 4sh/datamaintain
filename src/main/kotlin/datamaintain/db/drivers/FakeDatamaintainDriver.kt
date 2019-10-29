package datamaintain.db.drivers

import datamaintain.Script
import datamaintain.ScriptWithContent

import datamaintain.report.ScriptExecutionReport

class FakeDatamaintainDriver: DatamaintainDriver {
    override fun executeScript(script: ScriptWithContent): ScriptExecutionReport {
        throw NotImplementedError("FakeDatamaintainDriver executeScript method should not be used")
    }

    override fun listExecutedScripts(): Sequence<Script> {
        throw NotImplementedError("FakeDatamaintainDriver listExecutedScripts method should not be used")
    }

    override fun markAsExecuted(script: Script) {
        throw NotImplementedError("FakeDatamaintainDriver markAsExecuted method should not be used")
    }

}