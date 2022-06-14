package datamaintain.db.driver.mongo.exception

import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.db.driver.mongo.spi.JsonMapperFactory

class DatamaintainMongoJsonMapperMoreThanOneImplFoundException(jsonMapperFactories: List<JsonMapperFactory>):
    DatamaintainBaseException(
        "MongoDriverConfig : found more than one json mapper implementation. " +
                "Found : ${jsonMapperFactories.joinToString(", ") { it::class.java.name }}",
        "Load only one driver-mongo-mapping module or force the implementation by passing it to MongoDriverConfig"
    )
