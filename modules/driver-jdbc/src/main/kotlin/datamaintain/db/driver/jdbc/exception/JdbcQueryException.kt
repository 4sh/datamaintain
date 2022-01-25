package datamaintain.db.driver.jdbc.exception

import datamaintain.core.util.exception.DatamaintainQueryException
import java.sql.PreparedStatement
import java.sql.SQLException

class JdbcQueryException(insertStmt: PreparedStatement, e: SQLException)
    : DatamaintainQueryException("Query $insertStmt fail with exit code ${e.errorCode} an output : ${e.message}")
