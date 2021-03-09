package datamaintain.core.script

enum class ExecutionStatus {
    OK,
    KO,
    @Deprecated("Will be removed in 2.0. Useless now we store action on script")
    FORCE_MARKED_AS_EXECUTED;
}