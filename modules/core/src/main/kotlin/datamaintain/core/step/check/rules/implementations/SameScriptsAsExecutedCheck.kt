package datamaintain.core.step.check.rules.implementations

import datamaintain.core.exception.DatamaintainCheckException
import datamaintain.core.step.check.rules.contracts.FullContextCheckRule
import datamaintain.domain.ScriptType
import datamaintain.domain.script.LightExecutedScript
import datamaintain.domain.script.ScriptWithContent

class SameScriptsAsExecutedCheck(
        executedScripts: Sequence<LightExecutedScript>
) : FullContextCheckRule(executedScripts) {
    override fun check(scripts: Sequence<ScriptWithContent>) {
        val executedScriptChecksumsNotFoundInScannedScripts = executedScripts
                .map { it.checksum }
                .minus(scripts.map { it.checksum })
                .toList()

        if (executedScriptChecksumsNotFoundInScannedScripts.isNotEmpty()) {
            val executedScriptNames = executedScripts
                    .filter { executedScriptChecksumsNotFoundInScannedScripts.contains(it.checksum) }
                    .map { it.name }
                    .toList()

            throw DatamaintainCheckException(
                getName(),
                "Some executed scripts are not present : $executedScriptNames",
                resolutionMessage = "Please restore executed scripts or disable check ${getName()}"
            )
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
        val NAME = "SameScriptsAsExecutedCheck"
    }
}
