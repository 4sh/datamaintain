package datamaintain.core.exception

import datamaintain.core.script.ExecutedScript

class DatamaintainScriptExecutionException (
    executedScript: ExecutedScript
) : DatamaintainBaseException("${executedScript.name} has not been correctly executed")
