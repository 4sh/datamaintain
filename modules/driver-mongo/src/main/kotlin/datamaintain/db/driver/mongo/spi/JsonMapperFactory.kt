package datamaintain.db.driver.mongo.spi

import datamaintain.db.driver.mongo.JsonMapper
import datamaintain.db.driver.mongo.exception.DatamaintainMongoJsonMapperNotFoundException
import datamaintain.db.driver.mongo.exception.DatamaintainMongoJsonMapperMoreThanOneImplFoundException
import java.util.ServiceLoader

/**
 * Load JsonMapper implementation and return first
 * @throws DatamaintainMongoJsonMapperMoreThanOneImplFoundException If more than one implementation is found
 * @throws DatamaintainMongoJsonMapperNotFoundException If no implementation found
 */
val SPI_JSON_MAPPER by lazy {
    val jsonMapperFactories = ServiceLoader.load(JsonMapperFactory::class.java).toList()

    if (jsonMapperFactories.isEmpty()) {
        throw DatamaintainMongoJsonMapperNotFoundException()
    } else if (jsonMapperFactories.size > 1) {
        throw DatamaintainMongoJsonMapperMoreThanOneImplFoundException(jsonMapperFactories)
    }

    jsonMapperFactories.first().create()
}

interface JsonMapperFactory {
    fun create(): JsonMapper
}
