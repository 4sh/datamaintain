package datamaintain.core.report

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ScriptWithContent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Report(
        private val scannedScripts: List<ScriptWithContent> = listOf(),
        private val filteredScripts: List<ScriptWithContent> = listOf(),
        private val prunedScripts: List<ScriptWithContent> = listOf(),
        val executedScripts: List<ExecutedScript> = listOf(),
        private val scriptInError: ExecutedScript? = null
) {
    fun print() {
        logger.info { "Summary => " }
        logger.info { "- ${scannedScripts.size} files scanned" }
        logger.info { "- ${filteredScripts.size} files filtered" }
        logger.info { "- ${prunedScripts.size} files pruned" }
        logger.info { "- ${executedScripts.size} files executed" }
        executedScripts.forEach {logger.info { " -> ${it.name}" }}
        if (scriptInError != null) {
            logger.info { "- but last executed script is in error : ${scriptInError.name}" }
        }
    }
}


class ReportBuilder(
        private val scannedScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val filteredScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val prunedScripts: MutableList<ScriptWithContent> = mutableListOf(),
        private val executedScripts: MutableList<ExecutedScript> = mutableListOf(),
        private var scriptInError: ExecutedScript? = null
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

    fun addExecutedScript(script: ExecutedScript) {
        executedScripts.add(script)
    }

    fun inError(script: ExecutedScript) {
        scriptInError = script
    }

    fun toReport() = Report(
            scannedScripts,
            filteredScripts,
            prunedScripts,
            executedScripts,
            scriptInError
    )
}