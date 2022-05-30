package datamaintain.db.driver.mongo.mapping

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.LightExecutedScript

fun LightExecutedScriptDb.toLightExecutedScript() = LightExecutedScript(
    name!!,
    checksum!!,
    identifier!!
)

fun ExecutedScript.toExecutedScriptDb() = ExecutedScriptDb(
    name = name,
    checksum = checksum,
    identifier = identifier,
    executionStatus = executionStatus,
    action = action,
    executionDurationInMillis = executionDurationInMillis,
    executionOutput = executionOutput,
    flags = flags
)
