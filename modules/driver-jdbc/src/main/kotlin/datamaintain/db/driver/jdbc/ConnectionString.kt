package datamaintain.db.driver.jdbc

class ConnectionString {
    companion object {
        private val JDBC_URI_REGEX = Regex("^jdbc:(\\w*:){1,2}(//)?(?:([-._\\w]+):(.+)@)?([-.\\w]+)(?::([0-9]+))?(?:/([\\w-_]+)(?:\\?([\\w_.]+=[\\w_]+))?)")

        @JvmStatic
        fun buildConnectionString(jdbcpUri: String): String {
            if(JDBC_URI_REGEX.matches(jdbcpUri)) {
                return jdbcpUri
            }

            throw IllegalArgumentException("JDBC is not correct. The expected format is: jdbc:dbms:[//][username:password][@]host[:port]/databasename[?options]")
        }
    }
}