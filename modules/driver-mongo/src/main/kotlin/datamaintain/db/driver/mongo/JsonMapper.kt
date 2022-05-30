package datamaintain.db.driver.mongo

interface JsonMapper {
    fun <T> fromJson(json: String, clazz: Class<T>): T?
    fun toJson(aObject: Any): String
}
