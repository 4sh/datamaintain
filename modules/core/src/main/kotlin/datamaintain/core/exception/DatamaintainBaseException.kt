package datamaintain.core.exception

open class DatamaintainBaseException(
    override val message: String,
    open val resolutionMessage: String = "",
    override val cause: Throwable? = null
) : Exception(message, cause)
