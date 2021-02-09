package datamaintain.core.exception

class DatamaintainMongoUriException (
    mongoUri: String
) : DatamaintainBaseException("MongoUri $mongoUri is not correct. The expected format is: mongodb://[username:password@]host[:port]/databasename[?options]")
