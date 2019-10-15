package datamaintain

class TestScriptWithContent(override val name: String) : ScriptWithContent {
    override val checksum: String
        get() = name.hashCode().toString()
    override val content: String
        get() = ""
}