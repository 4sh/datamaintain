package datamaintain.domain.script

data class Tag(
        val name: String
) {
    infix fun isIncluded(script: ScriptWithContent): Boolean {
        return script.tags.any { it == this }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tag

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}