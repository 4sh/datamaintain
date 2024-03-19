package datamaintain.test

import datamaintain.domain.script.ScriptAction
import datamaintain.domain.script.ScriptWithContent
import datamaintain.domain.script.Tag

class TestScriptWithContent(
        override val name: String,
        override val identifier: String,
        override val tags: Set<Tag> = setOf(),
        override var action: ScriptAction = ScriptAction.RUN,
        override val porcelainName: String
) : ScriptWithContent {
    override val checksum: String
        get() = name.hashCode().toString()
    override val content: String
        get() = ""
}
