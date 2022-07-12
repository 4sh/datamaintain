package datamaintain.test

import datamaintain.domain.script.ScriptAction
import datamaintain.domain.script.ScriptWithContent
import datamaintain.domain.script.Tag

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
