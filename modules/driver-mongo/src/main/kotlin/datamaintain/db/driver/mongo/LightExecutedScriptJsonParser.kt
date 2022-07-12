package datamaintain.db.driver.mongo

import datamaintain.domain.script.LightExecutedScript

interface LightExecutedScriptJsonParser {
    // parse a stringify json array to an Array of LightExecutedScript
    fun parseArrayOfLightExecutedScripts(lightExecutedScriptJsonArray: String): Sequence<LightExecutedScript>
}
