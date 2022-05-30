package datamaintain.db.driver.mongo.gson

import com.google.gson.Gson
import datamaintain.db.driver.mongo.JsonMapper


class GsonMapper(private val mapper: Gson = DEFAULT_OBJECT_MAPPER): JsonMapper {
    override fun <T> fromJson(json: String, clazz: Class<T>): T = mapper.fromJson(json, clazz)

    override fun toJson(aObject: Any): String = mapper.toJson(aObject)

    companion object {
        val DEFAULT_OBJECT_MAPPER: Gson = Gson()
    }
}
