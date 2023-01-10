package datamaintain.db.driver.mongo

import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ExecutionStatus
import datamaintain.domain.script.ScriptAction

fun buildExecutedScript(
    name: String = "script3.js",
    checksum: String = "d3d9446802a44259755d38e6d163e820",
    identifier: String = "",
    executionStatus: ExecutionStatus = ExecutionStatus.OK,
    action: ScriptAction = ScriptAction.RUN,
    executionDurationInMillis: Long = 0,
    executionOutput: String = "test"
): ExecutedScript = ExecutedScript(
    name = name,
    checksum = checksum,
    identifier = identifier,
    executionStatus = executionStatus,
    action = action,
    executionDurationInMillis = executionDurationInMillis,
    executionOutput = executionOutput
)