package datamaintain.db.driver.mongo.spi

import datamaintain.db.driver.mongo.JsonMapper
import datamaintain.db.driver.mongo.serialization.SerializationMapper

class SerializationJsonMapperFactory: JsonMapperFactory {
    override fun create(): JsonMapper {
        return SerializationMapper()
    }
}
