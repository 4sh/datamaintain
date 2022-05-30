package datamaintain.db.driver.mongo.spi

import datamaintain.db.driver.mongo.JsonMapper
import datamaintain.db.driver.mongo.gson.GsonMapper

class GsonJsonMapperFactory: JsonMapperFactory {
    override fun create(): JsonMapper {
        return GsonMapper()
    }
}
