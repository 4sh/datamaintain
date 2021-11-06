package datamaintain.db.driver.mongo

enum class MongoShell {
    MONGO,  // mongo command line. The CLI is deprecated since Mongo 5.0
    MONGOSH; // mongosh command line. Default CLI since Mongo 5.0

    fun defaultBinaryName(): String {
        return name.toLowerCase()
    }

    companion object {
        fun fromNullable(name: String?, defaultMode: MongoShell): MongoShell {
            return if (name != null) {
                valueOf(name)
            } else {
                defaultMode
            }
        }
    }
}
