package datamaintain.db.driver.mongo

import datamaintain.core.script.ExecutedScript

interface ExecutedScriptBsonParser {
    // parse a stringify bson array to an Array of ExecutedScript
    fun parseArrayOfExecutedScripts(executedScriptJsonArray: String): Sequence<ExecutedScript>

    // Serialize an ExecutedScript to a stringify bson document
    fun serializeExecutedScript(executedScript:ExecutedScript): String
}