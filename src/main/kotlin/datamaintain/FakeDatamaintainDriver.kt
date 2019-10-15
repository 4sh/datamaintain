package datamaintain

class FakeDatamaintainDriver: DatamaintainDriver {
    override fun executeScript(script: ScriptWithContent): ScriptExecutionReport {
        throw NotImplementedError("FakeDatamaintainDriver executeScript method should not be used")
    }

    override fun listExecutedScripts(): List<Script> {
        throw NotImplementedError("FakeDatamaintainDriver listExecutedScripts method should not be used")
    }

    override fun markAsExecuted(script: Script) {
        throw NotImplementedError("FakeDatamaintainDriver markAsExecuted method should not be used")
    }

}