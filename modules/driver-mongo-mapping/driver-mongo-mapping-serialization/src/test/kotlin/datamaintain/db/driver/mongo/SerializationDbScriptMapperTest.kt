package datamaintain.db.driver.mongo

import datamaintain.db.driver.mongo.serialization.SerializationMapper


internal class SerializationDbScriptMapperTest: JsonMapperTest(SerializationMapper())
