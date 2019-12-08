package datamaintain.test

import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.Tag

class TestScriptWithContent(
        override val name: String,
        override val identifier: String,
        override val tags: Set<Tag> = setOf()
) : ScriptWithContent {
    override val checksum: String
        get() = name.hashCode().toString()
    override val content: String
        get() = ""
}