package datamaintain.db.driver.mongo.serialization

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.LightExecutedScript
import datamaintain.core.script.ScriptAction
import datamaintain.db.driver.mongo.ExecutedScriptJsonParser
import datamaintain.db.driver.mongo.LightExecutedScriptJsonParser
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*

// Copy of LightExecutedScript, this is aim for add the Serializable annotation
// Annotation allow to serialize/deserialize this object to/from a bson document (support json only)
@Serializable
data class LightExecutedScriptDb(@SerialName("_id") @ContextualSerialization val id: String = UUID.randomUUID().toString(),
                            val name: String,
                            val checksum: String,
                            val identifier: String)

// Mapping function
fun LightExecutedScriptDb.toLightExecutedScript() = LightExecutedScript(
    name,
    checksum,
    identifier
)
fun LightExecutedScript.toLightExecutedScriptDb() = LightExecutedScriptDb(
    name = name,
    checksum = checksum,
    identifier = identifier
)

// Copy of ExecutedScript, this is aim for add the Serializable annotation
// Annotation allow to serialize/deserialize this object to/from a bson document (support json only)
@Serializable
data class ExecutedScriptDb(@SerialName("_id") @ContextualSerialization val id: String = UUID.randomUUID().toString(),
                            val name: String,
                            val checksum: String,
                            val identifier: String,
                            val executionStatus: ExecutionStatus,
                            val action: ScriptAction? = null,
                            val executionDurationInMillis: Long? = null,
                            val executionOutput: String? = null)

// Mapping function
fun ExecutedScriptDb.toExecutedScript() = ExecutedScript(
    name,
    checksum,
    identifier,
    executionStatus,
    action,
    executionDurationInMillis,
    executionOutput
)
fun ExecutedScript.toExecutedScriptDb() = ExecutedScriptDb(
    name = name,
    checksum = checksum,
    identifier = identifier,
    executionStatus = executionStatus,
    action = action,
    executionDurationInMillis = executionDurationInMillis,
    executionOutput = executionOutput
)

class KJsonParser: ExecutedScriptJsonParser, LightExecutedScriptJsonParser {
    // Mapper between json and object
    val configuration = JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)
    private val mapper = Json(configuration)

    override fun parseArrayOfExecutedScripts(executedScriptJsonArray: String): Sequence<ExecutedScript> {

        return mapper.parse(ExecutedScriptDb.serializer().list, executedScriptJsonArray)
                .map { it.toExecutedScript() }
                .asSequence()
    }

    override fun serializeExecutedScript(executedScript: ExecutedScript): String {
        val executedScriptDb = executedScript.toExecutedScriptDb()

        return mapper.stringify(ExecutedScriptDb.serializer(), executedScriptDb)
    }

    override fun parseArrayOfLightExecutedScripts(lightExecutedScriptJsonArray: String): Sequence<LightExecutedScript> {
        return mapper.parse(LightExecutedScriptDb.serializer().list, lightExecutedScriptJsonArray)
            .map { it.toLightExecutedScript() }
            .asSequence()
    }

    override fun serializeLightExecutedScript(lightExecutedScript: LightExecutedScript): String {
        val lightExecutedScriptDb = lightExecutedScript.toLightExecutedScriptDb()

        return mapper.stringify(LightExecutedScriptDb.serializer(), lightExecutedScriptDb)
    }
}
