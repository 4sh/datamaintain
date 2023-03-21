package datamaintain.db.driver.mongo.mapping

import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.LightExecutedScript

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
