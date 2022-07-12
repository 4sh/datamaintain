package datamaintain.core.db.driver

enum class DBType(val filenameRegex: Regex) {
    MONGO("^.*\\.js$".toRegex()),
    JDBC("^.*\\.sql$".toRegex());

    override fun toString(): String = this.name.lowercase()

    companion object {
        fun tryFindFromString(dbTypeString: String) = DBType.values().firstOrNull { it.toString() == dbTypeString }
    }
}
