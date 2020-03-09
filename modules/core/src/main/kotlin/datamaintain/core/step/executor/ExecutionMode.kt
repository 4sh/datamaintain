package datamaintain.core.step.executor

enum class ExecutionMode {
    NORMAL,
    DRY,
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
