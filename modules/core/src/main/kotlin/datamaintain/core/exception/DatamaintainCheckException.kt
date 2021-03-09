package datamaintain.core.exception

class DatamaintainCheckException (
    checkName: String,
    checkErrorMessage: String,
    override val resolutionMessage: String = ""
) : DatamaintainBaseException("ERROR - $checkName - $checkErrorMessage", resolutionMessage)
