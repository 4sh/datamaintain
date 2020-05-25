package datamaintain.db.driver.mongo

import com.github.jershell.kbson.KBson
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import kotlinx.serialization.*
import org.bson.BsonArray
import org.bson.BsonDocument
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bson.types.ObjectId

// Copy of ExecutedScript, this is aim for add the Serializable annotation
// Annotation allow to serialize/deserialize this object to/from a bson document
@Serializable
data class ExecutedScriptDb(@SerialName("_id") @ContextualSerialization val id: ObjectId = ObjectId(),
                            val name: String,
                            val checksum: String,
                            val identifier: String,
                            val executionStatus: ExecutionStatus,
                            val executionOutput: String? = null)

// Mapping function
fun ExecutedScriptDb.toExecutedScript() = ExecutedScript(name, checksum, identifier, executionStatus, executionOutput)
fun ExecutedScript.toExecutedScriptDb() = ExecutedScriptDb(ObjectId(), name, checksum, identifier, executionStatus, executionOutput)

class KBsonParser: ExecutedScriptBsonParser {
    // Mapper between bson and object
    private val mapper = KBson()

    // force serialize in shell mode so ObjectId will be write
    // _id : ObjectId("xxx")
    // over json format
    // _id : {$oid: "xxx"}
    private val jsonMode = JsonWriterSettings.builder().outputMode(JsonMode.SHELL).build()

    override fun parseArrayOfExecutedScripts(executedScriptJsonArray: String): Sequence<ExecutedScript> {

        // Parse String to an Array of BsonDocument
        val doc = BsonArray.parse(executedScriptJsonArray)

        // For each document : parse to an ExecutedScriptDb then map to an ExecutedScript
        return doc.values
                .map { it as BsonDocument }
                .map { mapper.parse(ExecutedScriptDb.serializer(), it) }
                .map { it.toExecutedScript() }
                .asSequence()
    }

    override fun serializeExecutedScript(executedScript: ExecutedScript): String {
        val executedScriptDb = executedScript.toExecutedScriptDb()
        val bsonDocument = mapper.stringify(ExecutedScriptDb.serializer(), executedScriptDb)

        // Map bsonDocument to a bson string
        return bsonDocument.toJson(jsonMode)
    }
}
