package datamaintain.db.driver.mongo.exception

import datamaintain.core.exception.DatamaintainBaseException

class DatamaintainMongoClientNotFound(
    mongoExecutable: String,
) : DatamaintainBaseException(
    "Cannot find $mongoExecutable",
    "Check your command : is '$mongoExecutable --version' work ? " +
            "If mongo client is a command, check your PATH variable. " +
            "If mongo client is a path, please check the path exists."
)
