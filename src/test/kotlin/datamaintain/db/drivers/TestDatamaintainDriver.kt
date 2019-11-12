package datamaintain.db.drivers

import datamaintain.ScriptWithContent
import datamaintain.report.ExecutionLineReport
import datamaintain.report.ExecutionStatus
import java.time.Instant

class TestDatamaintainDriver(dbName: String, mongoUri: String) : MongoDatamaintainDriver(dbName, mongoUri) {
    override fun executeScript(script: ScriptWithContent): ExecutionLineReport {
        return ExecutionLineReport(Instant.now(), "", ExecutionStatus.OK, script)
    }
}
