package datamaintain.domain.report

import datamaintain.domain.CheckRule
import datamaintain.domain.script.ReportExecutedScript
import datamaintain.domain.script.ScriptWithContent

class Report @JvmOverloads constructor(
    val scannedScripts: List<ScriptWithContent> = listOf(),
    val filteredScripts: List<ScriptWithContent> = listOf(),
    val prunedScripts: List<ScriptWithContent> = listOf(),
    val executedScripts: List<ReportExecutedScript> = listOf(),
    val validatedCheckRules: List<CheckRule> = listOf()
) {

}


class ReportBuilder @JvmOverloads constructor(
    private val scannedScripts: MutableList<ScriptWithContent> = mutableListOf(),
    private val filteredScripts: MutableList<ScriptWithContent> = mutableListOf(),
    private val prunedScripts: MutableList<ScriptWithContent> = mutableListOf(),
    private val executedScripts: MutableList<ReportExecutedScript> = mutableListOf(),
    private val validatedCheckRules: MutableList<CheckRule> = mutableListOf()
) {

    fun addScannedScript(script: ScriptWithContent) {
        scannedScripts.add(script)
    }

    fun addFilteredScript(script: ScriptWithContent) {
        filteredScripts.add(script)
    }

    fun addPrunedScript(script: ScriptWithContent) {
        prunedScripts.add(script)
    }

    fun addReportExecutedScript(script: ReportExecutedScript) {
        executedScripts.add(script)
    }

    fun addValidatedCheckRules(checkRule: CheckRule) {
        validatedCheckRules.add(checkRule)
    }

    fun toReport() = Report(
        scannedScripts,
        filteredScripts,
        prunedScripts,
        executedScripts,
        validatedCheckRules
    )
}
