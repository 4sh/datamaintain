package datamaintain.core.exception

import datamaintain.core.util.exception.DatamaintainQueryException

class DatamaintainMongoQueryException(
    query: String,
    exitCode: Int,
    executionOutput: String?
) : DatamaintainQueryException(
    if (executionOutput != null && executionOutput.isNotEmpty())
        "Error while execute a query on mongodb : query : \"$query\" - exit code : $exitCode - query output : $executionOutput"
    else
        "Error while execute a query on mongodb : query : \"$query\" - exit code : $exitCode"
)
