package datamaintain.core.script

import datamaintain.core.step.executor.Execution

interface Script {
    val name: String
    val checksum: String
    val identifier: String

    fun fullName(): String {
        return name + identifier
    }
}

open class ExecutedScript @JvmOverloads constructor(
    override val name: String,
    override val checksum: String,
    override val identifier: String,
    open val executionStatus: ExecutionStatus,
    open var action: ScriptAction? = null,
    open val executionDurationInMillis: Long? = null,
    open val executionOutput: String? = null
) : Script {
    companion object {
        fun simulateExecuted(script: ScriptWithContent, executionStatus: ExecutionStatus) =
                ExecutedScript(
                        script.name,
                        script.checksum,
                        script.identifier,
                        executionStatus,
                        script.action
                )

        fun build(script: ScriptWithContent, execution: Execution) = simulateExecuted(script, execution.executionStatus)

        fun build(script: ScriptWithContent, execution: Execution, executionDurationInMillis: Long) =
                ExecutedScript(
                        script.name,
                        script.checksum,
                        script.identifier,
                        execution.executionStatus,
                        script.action,
                        executionDurationInMillis,
                        execution.executionOutput
                )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExecutedScript

        if (name != other.name) return false
        if (checksum != other.checksum) return false
        if (identifier != other.identifier) return false
        if (executionStatus != other.executionStatus) return false
        if (action != other.action) return false
        if (executionDurationInMillis != other.executionDurationInMillis) return false
        if (executionOutput != other.executionOutput) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + checksum.hashCode()
        result = 31 * result + identifier.hashCode()
        result = 31 * result + executionStatus.hashCode()
        result = 31 * result + (action?.hashCode() ?: 0)
        result = 31 * result + (executionDurationInMillis?.hashCode() ?: 0)
        result = 31 * result + (executionOutput?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ExecutedScript(name='$name', checksum='$checksum', identifier='$identifier', executionStatus=$executionStatus, action=$action, executionDurationInMillis=$executionDurationInMillis, executionOutput=$executionOutput)"
    }
}

data class ReportExecutedScript(
    override val name: String,
    override val checksum: String,
    override val identifier: String,
    override val executionStatus: ExecutionStatus,
    override var action: ScriptAction? = null,
    override val executionDurationInMillis: Long? = null,
    override val executionOutput: String? = null,
    val porcelainName: String
) : ExecutedScript(
        name,
        checksum,
        identifier,
        executionStatus,
        action,
        executionDurationInMillis,
        executionOutput
)

interface ScriptWithContent : Script {
    val content: String
    val tags: Set<Tag>
    var action: ScriptAction
    val porcelainName: String
}



