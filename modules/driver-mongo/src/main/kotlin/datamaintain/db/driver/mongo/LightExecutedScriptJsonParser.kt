package datamaintain.db.driver.mongo

import datamaintain.core.script.LightExecutedScript

interface LightExecutedScriptJsonParser {
    // parse a stringify json array to an Array of LightExecutedScript
    fun parseArrayOfLightExecutedScripts(lightExecutedScriptJsonArray: String): Sequence<LightExecutedScript>

    // Serialize an LightExecutedScript to a stringify json document
    fun serializeLightExecutedScript(lightExecutedScript: LightExecutedScript): String
}
