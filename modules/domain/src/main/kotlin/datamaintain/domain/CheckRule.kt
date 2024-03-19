package datamaintain.domain

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
