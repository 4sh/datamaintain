package datamaintain.core.exception

class DatamaintainMongoQueryException(
    query: String,
    exitCode: Int,
    executionOutput: String?
) : DatamaintainBaseException(
    if (executionOutput != null && executionOutput.isNotEmpty())
        "Error while execute a query on mongodb : query : \"$query\" - exit code : $exitCode - query output : $executionOutput"
    else
        "Error while execute a query on mongodb : query : \"$query\" - exit code : $exitCode"
)
