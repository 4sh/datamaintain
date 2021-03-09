package datamaintain.core.db.driver

open class ConnectionStringBuilder(regexPattern: String, private val errorMessage: String) {
    private val regex = Regex(regexPattern)

    fun buildConnectionString(uri: String, trustUri: Boolean): String {
        if(trustUri) {
            return uri
        }

        if(regex.matches(uri)) {
            return uri
        }

        throw IllegalArgumentException(errorMessage)
    }
}