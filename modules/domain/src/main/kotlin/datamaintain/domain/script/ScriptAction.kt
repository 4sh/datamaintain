package datamaintain.domain.script

enum class ScriptAction {

    /**
     * Indicates the script need to be executed and then marked as executed in DB.
     */
    RUN,

    /**
     * Indicates the script only need to be marked as executed in DB. No execution.
     */
    MARK_AS_EXECUTED,

    /**
     * Indicates to override an updated script that datamaintain will detect as already executed (based
     * on the script name). No execution.
     */
    OVERRIDE_EXECUTED;

    companion object {
        fun fromNullable(name: String?, defaultMode: ScriptAction): ScriptAction {
            return if (name != null) {
                valueOf(name)
            } else {
                defaultMode
            }
        }
    }

}