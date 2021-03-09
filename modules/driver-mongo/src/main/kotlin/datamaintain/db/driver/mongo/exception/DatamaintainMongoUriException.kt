package datamaintain.db.driver.mongo.exception

import datamaintain.core.exception.DatamaintainBaseException

class DatamaintainMongoUriException (
    mongoUri: String
) : DatamaintainBaseException("MongoUri $mongoUri is not correct. The expected format is: mongodb://[username:password@]host[:port]/databasename[?options]")
