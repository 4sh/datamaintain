package datamaintain.test

import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.Tag
import java.util.zip.Checksum

class ScriptWithContentWithFixedChecksum(
        override val name: String,
        override val identifier: String,
        override val checksum: String,
        override val tags: Set<Tag> = setOf()
) : ScriptWithContent {
    override val content: String
        get() = ""
}