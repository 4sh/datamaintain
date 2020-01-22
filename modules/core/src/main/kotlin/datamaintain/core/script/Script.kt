package datamaintain.core.script


interface Script {
    val name: String
    val checksum: String
    val identifier: String
}

data class ExecutedScript(
        override val name: String,
        override val checksum: String,
        override val identifier: String,
        val executionStatus: ExecutionStatus,
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
    }
}

interface ScriptWithContent : Script {
    val content: String
    val tags: Set<Tag>
}



