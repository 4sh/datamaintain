package datamaintain.core.step.check.rules.implementations

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.check.rules.ScriptType
import datamaintain.core.step.check.rules.contracts.FullContextCheckRule

class ExecutedScriptsNotRemovedCheck(
        executedScripts: Sequence<ExecutedScript>
) : FullContextCheckRule(executedScripts) {
    override fun check(scripts: Sequence<ScriptWithContent>) {
        val executedScriptChecksumsNotFoundInSortedScripts = executedScripts
                .map { it.checksum }
                .minus(scripts.map { it.checksum })
                .toList()

        if (executedScriptChecksumsNotFoundInSortedScripts.isNotEmpty()) {
            throw IllegalStateException("ERROR - ${getName()} - Some executed scripts are not present : " +
                    "$executedScriptChecksumsNotFoundInSortedScripts")
        }
    }

    override fun getName(): String {
        return NAME
    }

    override fun scriptType(): ScriptType {
        return ScriptType.SCANNED_SCRIPT
    }

    companion object {
        @JvmStatic
        val NAME = "ExecutedScriptsNotRemoved"
    }
}