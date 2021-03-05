package datamaintain.db.driver.jdbc

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.ScriptAction
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.Tag
import java.math.BigInteger
import java.security.MessageDigest

data class InMemoryScript(
        override val name: String,
        override val content: String,
        override val identifier: String,
        override val tags: Set<Tag> = setOf(),
        override var action: ScriptAction = DatamaintainConfig.defaultAction) : ScriptWithContent {

    override val checksum: String by lazy {
        content.hash()
    }

    private fun String.hash(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}