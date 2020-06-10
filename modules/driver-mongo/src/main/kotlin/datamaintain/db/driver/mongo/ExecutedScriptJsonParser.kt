package datamaintain.db.driver.mongo

import datamaintain.core.script.ExecutedScript

interface ExecutedScriptJsonParser {
    // parse a stringify json array to an Array of ExecutedScript
    fun parseArrayOfExecutedScripts(executedScriptJsonArray: String): Sequence<ExecutedScript>

    // Serialize an ExecutedScript to a stringify json document
    fun serializeExecutedScript(executedScript:ExecutedScript): String
}