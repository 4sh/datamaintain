package datamaintain.db.driver.mongo

class FakeJsonMapper: JsonMapper {
    override fun <T> fromJson(json: String, clazz: Class<T>): T? {
        throw NotImplementedError()
    }

    override fun toJson(aObject: Any): String {
        throw NotImplementedError()
    }
}

class FakeJsonMapperWithBadConstructor(val aObject: Object): JsonMapper {
    override fun <T> fromJson(json: String, clazz: Class<T>): T? {
        throw NotImplementedError()
    }

    override fun toJson(aObject: Any): String {
        throw NotImplementedError()
    }
}
