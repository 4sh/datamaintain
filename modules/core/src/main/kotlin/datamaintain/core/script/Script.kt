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

data class ExecutedScript @JvmOverloads constructor(
        override val name: String,
        override val checksum: String,
        override val identifier: String,
        val executionStatus: ExecutionStatus,
        var action: ScriptAction,
        val executionDurationInMillis: Long? = null,
        val executionOutput: String? = null
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
}

interface ScriptWithContent : Script {
    val content: String
    val tags: Set<Tag>
    var action: ScriptAction
}



