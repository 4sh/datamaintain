package datamaintain


interface Script {
    val name: String
    val checksum: String
}

data class ScriptWithoutContent(
        override val name: String,
        override val checksum: String
) : Script

interface ScriptWithContent : Script {
    val content: String
}



