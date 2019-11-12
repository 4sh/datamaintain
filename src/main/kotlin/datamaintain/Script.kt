package datamaintain


interface Script {
    val name: String
    val checksum: String
    val identifier: String
}

data class ScriptWithoutContent(
        override val name: String,
        override val checksum: String,
        override val identifier: String
) : Script

interface ScriptWithContent : Script {
    val content: String
}



