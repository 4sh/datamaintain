package datamaintain.core.script

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.config.DatamaintainExecutorConfig
import datamaintain.core.exception.DatamaintainFileIdentifierPatternException
import datamaintain.core.util.extractRelativePath
import datamaintain.domain.script.ScriptAction
import datamaintain.domain.script.ScriptWithContent
import datamaintain.domain.script.Tag
import java.io.File
import java.math.BigInteger
import java.nio.file.Path
import java.security.MessageDigest

class FileScript @JvmOverloads constructor(
        val path: Path,
        identifierRegex: Regex,
        override val tags: Set<Tag> = setOf(),
        override var action: ScriptAction = DatamaintainExecutorConfig.defaultAction,
        override val porcelainName: String? = null
) : ScriptWithContent {

    companion object {
        fun from(config: DatamaintainConfig, tags: Set<Tag>, scriptFile: File): FileScript {
            val path = scriptFile.toPath()
            return FileScript(
                path,
                config.scanner.identifierRegex,
                tags,
                config.executor.defaultScriptAction,
                computePorcelainName(config, path)
            )
        }

        private fun computePorcelainName(config: DatamaintainConfig, path: Path): String? =
            if(!config.logs.porcelain) {
                null
            } else {
                extractRelativePath(config.scanner.path, path)
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



