package datamaintain.db.driver.mongo

import datamaintain.core.exception.DatamaintainMongoUriException

class ConnectionString {
    companion object {
        private val MONGO_URI_REGEX = Regex("^mongodb(?:\\+srv)?://(?:([-._\\w]+):(.+)@)?([-.\\w]+)(?::([0-9]+))?(?:/([\\w-_]+)(?:\\?([\\w_.]+=[\\w_]+))?)")

        @JvmStatic
        fun buildConnectionString(mongoUri: String, trustUri: Boolean): String {
            if(trustUri) {
                return mongoUri
            }

            if(MONGO_URI_REGEX.matches(mongoUri)) {
                return mongoUri
            }

            throw DatamaintainMongoUriException(mongoUri)
        }
    }
}
