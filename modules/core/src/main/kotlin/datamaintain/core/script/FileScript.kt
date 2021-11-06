package datamaintain.core.script

import java.math.BigInteger
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainFileIdentifierPatternException
import datamaintain.core.util.extractRelativePath
import java.io.File
import java.nio.file.Path
import java.security.MessageDigest

class FileScript @JvmOverloads constructor(
        val path: Path,
        identifierRegex: Regex,
        override val tags: Set<Tag> = setOf(),
        override var action: ScriptAction = DatamaintainConfig.defaultAction,
        override val porcelainName: String? = null
) : ScriptWithContent {

    companion object {
        fun from(config: DatamaintainConfig, tags: Set<Tag>, scriptFile: File): FileScript {
            val path = scriptFile.toPath()
            return FileScript(
                path,
                config.identifierRegex,
                tags,
                config.defaultScriptAction,
                computePorcelainName(config, path)
            )
        }

        private fun computePorcelainName(config: DatamaintainConfig, path: Path): String? =
            if(!config.porcelain) {
                null
            } else {
                extractRelativePath(config.path, path)
            }
    }

    override val name: String
        get() = path.fileName.toString()

    override val checksum: String by lazy {
        content.hash()
    }

    override val content: String by lazy {
        path.toFile().readText()
    }

    override val identifier: String by lazy(fun(): String {
        val matchResult =
            identifierRegex.matchEntire(name) ?: throw DatamaintainFileIdentifierPatternException(name, identifierRegex)

        return matchResult.groupValues[1]
    })

    private fun String.hash(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}



