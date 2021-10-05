package datamaintain.db.driver.mongo

import datamaintain.core.script.ExecutedScript

interface ExecutedScriptJsonParser {
    // Serialize an ExecutedScript to a stringify json document
    fun serializeExecutedScript(executedScript:ExecutedScript): String
}
