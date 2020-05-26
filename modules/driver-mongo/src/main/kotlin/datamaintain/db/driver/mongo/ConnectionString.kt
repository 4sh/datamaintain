package datamaintain.db.driver.mongo

class ConnectionString {
    companion object {
        private val MONGO_URI_REGEX = Regex("^mongodb(?:\\+srv)?://(?:([-._\\w]+):(.+)@)?([-.\\w]+)(?::([0-9]+))?(?:/([\\w-_]+)(?:\\?([\\w_.]+=[\\w_]+))?)")

        @JvmStatic
        fun buildConnectionString(mongoUri: String): String {
            if(MONGO_URI_REGEX.matches(mongoUri)) {
                return mongoUri
            }

            throw IllegalArgumentException("MongoUri is not correct. The expected format is: mongodb://[username:password@]host[:port]/databasename[?options]")
        }
    }
}