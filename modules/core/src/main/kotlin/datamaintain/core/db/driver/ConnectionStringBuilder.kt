package datamaintain.core.db.driver

import datamaintain.core.exception.DatamaintainBaseException

open class ConnectionStringBuilder(regexPattern: String, private val exceptionBuilder: (String) -> DatamaintainBaseException) {
    private val regex = Regex(regexPattern)

    fun buildConnectionString(uri: String, trustUri: Boolean): String {
        if(trustUri) {
            return uri
        }

        if(regex.matches(uri)) {
            return uri
        }

        throw exceptionBuilder(uri)
    }
}