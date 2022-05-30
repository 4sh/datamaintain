package datamaintain.db.driver.mongo.spi

import datamaintain.db.driver.mongo.JsonMapper
import datamaintain.db.driver.mongo.exception.DatamaintainMongoJsonMapperNotFoundException
import java.util.ServiceLoader

/**
 * Load JsonMapperProvider implementation and return first
 * @throws DatamaintainMongoJsonMapperNotFoundException If no implementation found
 */
val SPI_JSON_MAPPER by lazy {
    ServiceLoader.load(JsonMapperFactory::class.java).iterator()
        .asSequence()
        .firstOrNull()
        ?.create()
        ?: throw DatamaintainMongoJsonMapperNotFoundException()
}

interface JsonMapperFactory {
    fun create(): JsonMapper
}
