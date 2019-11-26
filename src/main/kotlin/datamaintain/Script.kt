package datamaintain


interface Script {
    val name: String
    val checksum: String
    val identifier: String
    val tags: Set<Tag>
}

data class ScriptWithoutContent(
        override val name: String,
        override val checksum: String,
        override val identifier: String,
        override val tags: Set<Tag> = setOf()
) : Script

interface ScriptWithContent : Script {
    val content: String
}



