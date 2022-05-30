package datamaintain.db.driver.mongo.exception

import datamaintain.core.exception.DatamaintainBaseException

class DatamaintainMongoJsonMapperNotFoundException:
    DatamaintainBaseException(
        "MongoDriverConfig : json mapper implementation not found",
        "Add an artifact like driver-mongo-mapping-serialization or " +
                "implements your own JsonMapper and pass it to the MongoDriver config"
    )
