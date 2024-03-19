package datamaintain.domain.script

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

open class ExecutedScript @JvmOverloads constructor(
        override val name: String,
        override val checksum: String,
        override val identifier: String,
        open val executionStatus: ExecutionStatus,
        open var action: ScriptAction? = null,
        open val executionDurationInMillis: Long? = null,
        open val executionOutput: String? = null,
        open val flags: List<String> = listOf()
) : LightExecutedScript(name, checksum, identifier) {
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

    fun copy(
        name: String = this.name,
        checksum: String = this.checksum,
        identifier: String = this.identifier,
        executionStatus: ExecutionStatus = this.executionStatus,
        action: ScriptAction? = this.action,
        executionDurationInMillis: Long? = this.executionDurationInMillis,
        executionOutput: String? = this.executionOutput
    ) = ExecutedScript(
        name = name,
        checksum = checksum,
        identifier = identifier,
        executionStatus = executionStatus,
        action = action,
        executionDurationInMillis = executionDurationInMillis,
        executionOutput = executionOutput
    )
}

data class ReportExecutedScript(
    override val name: String,
    override val checksum: String,
    override val identifier: String,
    override val executionStatus: ExecutionStatus,
    override var action: ScriptAction? = null,
    override val executionDurationInMillis: Long? = null,
    override val executionOutput: String? = null,
    override val flags: List<String> = listOf(),
    val porcelainName: String? = null
) : ExecutedScript(
        name,
        checksum,
        identifier,
        executionStatus,
        action,
        executionDurationInMillis,
        executionOutput,
        flags
) {
    companion object {
        fun from(executedScript: ExecutedScript, porcelainName: String?) = ReportExecutedScript(
            executedScript.name,
            executedScript.checksum,
            executedScript.identifier,
            executedScript.executionStatus,
            executedScript.action,
            executedScript.executionDurationInMillis,
            executedScript.executionOutput,
            executedScript.flags,
            porcelainName
        )
    }
}

interface ScriptWithContent : Script {
    val content: String
    val tags: Set<Tag>
    var action: ScriptAction
    val porcelainName: String?
}



