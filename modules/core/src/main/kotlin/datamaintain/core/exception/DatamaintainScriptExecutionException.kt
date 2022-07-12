package datamaintain.core.exception

import datamaintain.domain.script.ExecutedScript

class DatamaintainScriptExecutionException (
    executedScript: ExecutedScript
) : DatamaintainBaseException("${executedScript.name} has not been correctly executed")
