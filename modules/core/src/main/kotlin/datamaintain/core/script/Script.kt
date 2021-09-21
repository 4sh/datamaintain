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

open class LightExecutedScript(
    override val name: String,
    override val checksum: String,
    override val identifier: String
) : Script {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LightExecutedScript

        if (name != other.name) return false
        if (checksum != other.checksum) return false
        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + checksum.hashCode()
        result = 31 * result + identifier.hashCode()
        return result
    }

    override fun toString(): String {
        return "LightExecutedScript(name='$name', checksum='$checksum', identifier='$identifier')"
    }
}

data class ExecutedScript @JvmOverloads constructor(
        override val name: String,
        override val checksum: String,
        override val identifier: String,
        val executionStatus: ExecutionStatus,
        var action: ScriptAction? = null,
        val executionDurationInMillis: Long? = null,
        val executionOutput: String? = null
) : LightExecutedScript(name, checksum, identifier) {
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
}

interface ScriptWithContent : Script {
    val content: String
    val tags: Set<Tag>
    var action: ScriptAction
}



