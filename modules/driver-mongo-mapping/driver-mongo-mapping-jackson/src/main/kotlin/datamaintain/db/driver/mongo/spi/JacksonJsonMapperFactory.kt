package datamaintain.db.driver.mongo.spi

import datamaintain.db.driver.mongo.JsonMapper
import datamaintain.db.driver.mongo.jackson.JacksonMapper

class JacksonJsonMapperFactory: JsonMapperFactory {
    override fun create(): JsonMapper {
        return JacksonMapper()
    }
}
