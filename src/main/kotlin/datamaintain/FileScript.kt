package datamaintain

import java.nio.file.Path

class FileScript(val path: Path) : ScriptWithContent {

    override val name: String
        get() = path.fileName.toString()

    override val checksum: String
        get() = content.hash()

    override val content: String by lazy {
        path.toFile().readText()
    }

    private fun String.hash(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}



