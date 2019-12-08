package datamaintain

data class Tag(val name: String) {
    infix fun matchedBy(script: ScriptWithContent): Boolean {
        return script.tags.any { it matches this }
    }

    infix fun matches(tag: Tag): Boolean {
        return name == tag.name
    }
}