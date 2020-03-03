package datamaintain.core.script

enum class ExecutionStatus {
    OK, KO, FORCE_MARKED_AS_EXECUTED, SHOULD_BE_EXECUTED;

    fun correctlyExecuted() = this == OK ||
            this == FORCE_MARKED_AS_EXECUTED ||
            this == SHOULD_BE_EXECUTED
}