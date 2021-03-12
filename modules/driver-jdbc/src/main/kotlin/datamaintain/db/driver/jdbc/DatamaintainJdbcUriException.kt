package datamaintain.db.driver.jdbc

import datamaintain.core.exception.DatamaintainBaseException

class DatamaintainJdbcUriException(uri: String): DatamaintainBaseException("JDBC uri ${uri} is not correct. The expected format is: jdbc:dbms:[//][username:password][@]host[:port]/databasename[?options]")