package datamaintain.db.driver.mongo.serialization

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.LightExecutedScript
import datamaintain.core.script.ScriptAction
import datamaintain.db.driver.mongo.ExecutedScriptJsonParser
import datamaintain.db.driver.mongo.LightExecutedScriptJsonParser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

// Copy of LightExecutedScript, this is aim for add the Serializable annotation
// Annotation allow to serialize/deserialize this object to/from a bson document (support json only)
@Serializable
data class LightExecutedScriptDb(@SerialName("_id") val id: String = UUID.randomUUID().toString(),
                            val name: String,
                            val checksum: String,
                            val identifier: String)

// Mapping function
fun LightExecutedScriptDb.toLightExecutedScript() = LightExecutedScript(
    name,
    checksum,
    identifier
)

// Copy of ExecutedScript, this is aim for add the Serializable annotation
// Annotation allow to serialize/deserialize this object to/from a bson document (support json only)
@Serializable
data class ExecutedScriptDb(@SerialName("_id") val id: String = UUID.randomUUID().toString(),
                            val name: String,
                            val checksum: String,
                            val identifier: String,
                            val executionStatus: ExecutionStatus,
                            val action: ScriptAction? = null,
                            val executionDurationInMillis: Long? = null,
                            val executionOutput: String? = null,
                            val flags: List<String> = listOf()
)

// Mapping function
fun ExecutedScriptDb.toExecutedScript() = ExecutedScript(
    name,
    checksum,
    identifier,
    executionStatus,
    action,
    executionDurationInMillis,
    executionOutput,
    flags
)
fun ExecutedScript.toExecutedScriptDb() = ExecutedScriptDb(
    name = name,
    checksum = checksum,
    identifier = identifier,
    executionStatus = executionStatus,
    action = action,
    executionDurationInMillis = executionDurationInMillis,
    executionOutput = executionOutput,
    flags = flags
)

class KJsonParser: ExecutedScriptJsonParser, LightExecutedScriptJsonParser {
    // Mapper between json and object
    private val mapper = Json { ignoreUnknownKeys = true }

    override fun serializeExecutedScript(executedScript: ExecutedScript): String {
        val executedScriptDb = executedScript.toExecutedScriptDb()

        return mapper.encodeToString(executedScriptDb)
    }

    override fun parseArrayOfLightExecutedScripts(lightExecutedScriptJsonArray: String): Sequence<LightExecutedScript> {
        return mapper.decodeFromString<List<LightExecutedScriptDb>>(lightExecutedScriptJsonArray)
            .map { it.toLightExecutedScript() }
            .asSequence()
    }
}
