package datamaintain.core.exception

class DatamaintainCheckException (
    override val message: String,
    val resolutionMessage: String = ""
): Exception(message)
