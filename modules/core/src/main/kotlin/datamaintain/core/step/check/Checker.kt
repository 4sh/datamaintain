package datamaintain.core.step.check

import datamaintain.core.Context
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.check.rules.CheckRule
import datamaintain.core.step.check.rules.ScriptType
import datamaintain.core.step.check.rules.contracts.FullContextCheckRule
import datamaintain.core.step.check.rules.contracts.ScriptCheckRule
import datamaintain.core.step.check.rules.contracts.ScriptWithContextCheckRule
import datamaintain.core.step.check.rules.implementations.ExecutedScriptsNotRemovedCheck
import mu.KotlinLogging
import java.lang.IllegalArgumentException

private val logger = KotlinLogging.logger {}

val allCheckRuleNames: Sequence<String> = sequenceOf(
        ExecutedScriptsNotRemovedCheck.NAME
)

class Checker(private val context: Context) {
    fun check(checkedData: CheckerData): List<ScriptWithContent> {
        logger.info { "Check scripts..." }

        // we want to ensure all rule can be built before to launch the first check,
        // so end the sequence stream by building a list.
        val rules = context.config.checkRules
                .map { buildCheckRule(it) }
                .toList()

        // All rules exist so we can launch them
        rules.forEach { executeRule(it, checkedData) }

        logger.info { "All check rules passed !" }
        logger.info { "" }

        // Checker doesn't have responsability to add or remove script.
        // So if all checks passed then return the prunedScripts
        return checkedData.prunedScripts.toList()
    }

    private fun buildCheckRule(ruleName: String): CheckRule {
        val executedScripts = context.dbDriver.listExecutedScripts()

        return when (ruleName) {
            ExecutedScriptsNotRemovedCheck.NAME -> ExecutedScriptsNotRemovedCheck(executedScripts)
            else -> throw IllegalArgumentException("Check rule `${ruleName}` not found")
        }
    }

     private fun executeRule(checkRule: CheckRule, checkerData: CheckerData) {
         logger.info { "Execute ${checkRule.getName()}" }

         val scripts = getScriptsFromCheckerDataByType(checkerData, checkRule.scriptType())

         when (checkRule) {
             is ScriptCheckRule -> scripts.forEach {
                 checkRule.check(it)
             }
             is ScriptWithContextCheckRule -> scripts.forEach {
                 checkRule.check(it)
             }
             is FullContextCheckRule -> {
                 checkRule.check(scripts)
             }
         }

         logger.info { "${checkRule.getName()} executed" }
     }

    private fun getScriptsFromCheckerDataByType(checkerData: CheckerData, scriptType: ScriptType): Sequence<ScriptWithContent> {
        return when (scriptType) {
            ScriptType.SCANNED_SCRIPT -> checkerData.scannedScripts
            ScriptType.FILTERED_SCRIPT -> checkerData.filteredScripts
            ScriptType.SORTED_SCRIPT -> checkerData.sortedScripts
            ScriptType.PRUNED_SCRIPT -> checkerData.prunedScripts
        }
    }
}

data class CheckerData(var scannedScripts: Sequence<ScriptWithContent> = emptySequence(),
                       var filteredScripts: Sequence<ScriptWithContent> = emptySequence(),
                       var sortedScripts: Sequence<ScriptWithContent> = emptySequence(),
                       var prunedScripts: Sequence<ScriptWithContent> = emptySequence())