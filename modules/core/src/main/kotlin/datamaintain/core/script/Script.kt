package datamaintain.core.script

import datamaintain.core.step.executor.Execution

import java.time.Duration
import java.time.LocalDateTime

interface Script {
    val name: String
    val checksum: String
    val identifier: String
}

data class ExecutedScript @JvmOverloads constructor(
        override val name: String,
        override val checksum: String,
        override val identifier: String,
        val executionStatus: ExecutionStatus,
        val executionDuration: Duration? = null,
        val executionOutput: String? = null
) : Script {
    companion object {
        fun forceMarkAsExecuted(script: ScriptWithContent) =
                ExecutedScript(
                        script.name,
                        script.checksum,
                        script.identifier,
                        ExecutionStatus.FORCE_MARKED_AS_EXECUTED
                )

        fun shouldBeExecuted(script: ScriptWithContent) =
                ExecutedScript(
                        script.name,
                        script.checksum,
                        script.identifier,
                        ExecutionStatus.SHOULD_BE_EXECUTED
                )

        fun build(script: ScriptWithContent, execution: Execution, executionStartDate: LocalDateTime, executionEndDate: LocalDateTime) =
                ExecutedScript(
                        script.name,
                        script.checksum,
                        script.identifier,
                        execution.executionStatus,
                        Duration.between(executionEndDate, executionStartDate),
                        execution.executionOutput
                )
    }
}

interface ScriptWithContent : Script {
    val content: String
    val tags: Set<Tag>
}



