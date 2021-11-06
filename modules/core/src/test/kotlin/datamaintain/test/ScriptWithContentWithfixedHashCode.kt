package datamaintain.test

import datamaintain.core.script.ScriptAction
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.Tag

class ScriptWithContentWithFixedChecksum(
        override val name: String,
        override val identifier: String,
        override val checksum: String,
        override val tags: Set<Tag> = setOf(),
        override var action: ScriptAction = ScriptAction.RUN,
        override val porcelainName: String = ""
) : ScriptWithContent {
    override val content: String
        get() = ""
}
