package datamaintain.db.driver.mongo.exception

import datamaintain.core.exception.DatamaintainBaseException

class DatamaintainMongoJsonMapperInstanceException(cause: Exception):
    DatamaintainBaseException("MongoDriverConfig : Cannot create an instance of JsonMapper implementation", cause = cause)
