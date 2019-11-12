package datamaintain.db.drivers

import datamaintain.ScriptWithContent
import datamaintain.TestScriptLineReport
import datamaintain.report.ExecutionStatus
import datamaintain.report.ScriptLineReport
import java.time.Instant

class TestDatamaintainDriver(dbName: String, mongoUri: String) : MongoDatamaintainDriver(dbName, mongoUri) {
    override fun executeScript(script: ScriptWithContent): ScriptLineReport {
        return TestScriptLineReport(Instant.now(), "", ExecutionStatus.OK)
    }
}
