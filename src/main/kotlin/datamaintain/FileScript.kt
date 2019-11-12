package datamaintain

import java.math.BigInteger
import java.nio.file.Path
import java.security.MessageDigest

class FileScript constructor(val path: Path, identifierRegex: Regex) : ScriptWithContent {

    override val name: String
        get() = path.fileName.toString()

    override val checksum: String by lazy {
        content.hash()
    }

    override val content: String by lazy {
        path.toFile().readText()
    }

    override val identifier: String by lazy(fun(): String {
        val matchResult = identifierRegex.matchEntire(name) ?: return ""
        return matchResult.groupValues[1]
    })

    private fun String.hash(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}



