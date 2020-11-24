package datamaintain.core.step.check.rules

import datamaintain.core.step.check.rules.implementations.ExecutedScriptsNotRemovedCheck

interface CheckRule {
    fun getName(): String
    fun scriptType(): ScriptType
}

enum class ScriptType {
    SCANNED_SCRIPT,
    FILTERED_SCRIPT,
    SORTED_SCRIPT,
    PRUNED_SCRIPT,
}
