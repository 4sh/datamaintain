package datamaintain.core.step.executor

enum class ExecutionMode {
    NORMAL,
    DRY,
    @Deprecated(message = "Will be removed in 2.0. Use ScriptAction.MARK_AS_EXECUTED to manage FORCE_MARK_AS_EXECUTED")
    FORCE_MARK_AS_EXECUTED;

    companion object {
        fun fromNullable(name: String?, defaultMode: ExecutionMode): ExecutionMode {
            return if (name != null) {
                valueOf(name)
            } else {
                defaultMode
            }
        }
    }
}
