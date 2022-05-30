package datamaintain.db.driver.mongo.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import datamaintain.db.driver.mongo.JsonMapper

class JacksonMapper(private val mapper: ObjectMapper = DEFAULT_OBJECT_MAPPER): JsonMapper {
    override fun <T> fromJson(json: String, clazz: Class<T>): T = mapper.readValue(json, clazz)

    override fun toJson(aObject: Any): String = mapper.writeValueAsString(aObject)

    companion object {
        val DEFAULT_OBJECT_MAPPER: ObjectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
