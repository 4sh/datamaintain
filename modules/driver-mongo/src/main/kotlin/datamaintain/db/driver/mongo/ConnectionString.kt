package datamaintain.db.driver.mongo

class ConnectionString {
    companion object {
        private val MONGO_URI_REGEX = Regex("^mongodb(?:\\+srv)?://(?:([-._\\w]+):(.+)@)?([-\\w]+)(?::([0-9]+))?(?:/([\\w-_]+)(?:\\?([\\w_.]+=[\\w_]+))?)")

        @JvmStatic
        fun buildConnectionString(mongoUri: String): String {
            val matchResult = MONGO_URI_REGEX.matchEntire(mongoUri)
                    ?: throw IllegalArgumentException("MongoUri is not correct. The expected format is: mongodb://[username:password@]host[:port]/databasename[?options]")


            val (_, _, host, port, username, password, database) = matchResult.destructured
//        val connectionString = ConnectionString(mongoUri)
//        // mongoUri can come with a database but currently driver's dbName is mandatory
//        if (connectionString.database == null) {
//            throw IllegalArgumentException("MongoUri does not contains a database name")
//        }
//        // mongoUri can come with a collection. It has no sense in DataMaintain's logic
//        if (connectionString.collection != null) {
//            throw IllegalArgumentException("MongoUri contains a collection name, please remove it")
//        }
            return mongoUri
        }
    }
}