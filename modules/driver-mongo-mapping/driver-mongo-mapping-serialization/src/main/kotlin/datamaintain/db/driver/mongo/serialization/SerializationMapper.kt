package datamaintain.db.driver.mongo.serialization

import datamaintain.db.driver.mongo.JsonMapper
import datamaintain.db.driver.mongo.mapping.ExecutedScriptDb
import datamaintain.db.driver.mongo.mapping.LightExecutedScriptDb
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SerializationMapper(private val mapper: Json = Json { ignoreUnknownKeys = true }) : JsonMapper {
    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        if (clazz == Array<LightExecutedScriptDb>::class.java) {
            return fromJsonToLightExecutedScriptArray(json) as T
        } else {
            throw IllegalStateException("Cannot deserialize class ${clazz.name}")
        }
    }

    override fun toJson(aObject: Any): String {
        if (aObject is ExecutedScriptDb) {
            return executedScriptDbToJson(aObject)
        } else {
            throw IllegalStateException("Cannot serialize class ${aObject::javaClass.name}")
        }
    }

    private fun fromJsonToLightExecutedScriptArray(json: String) =
        mapper.decodeFromString<Array<SerializationLightExecutedScriptDb>>(json)
            .map { it.toLightExecutedScriptDb() }
            .toTypedArray()

    private fun executedScriptDbToJson(executedScriptDb: ExecutedScriptDb): String =
        mapper.encodeToString(executedScriptDb.toSerializationExecutedScriptDb())
}
