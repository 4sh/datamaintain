package datamaintain.core.exception

import datamaintain.core.util.runProcess

class DatamaintainProcessException (
    command: List<String>,
    messageError: String
) : DatamaintainBaseException("Error while executed command : \"${command.joinToString(" ")}\" - $messageError ")
