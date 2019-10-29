package datamaintain.db.drivers

import datamaintain.Script
import datamaintain.ScriptWithContent
import datamaintain.report.ExecutionLineReport

import datamaintain.report.ScriptLineReport


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