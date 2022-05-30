package datamaintain.db.driver.mongo.exception

import datamaintain.core.exception.DatamaintainBaseException

class DatamaintainMongoParserNullPointerException:
    DatamaintainBaseException("MongoDriverConfig : json mapper is mandatory")
