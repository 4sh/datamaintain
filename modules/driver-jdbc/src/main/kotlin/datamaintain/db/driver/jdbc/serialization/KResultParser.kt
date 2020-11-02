package datamaintain.db.driver.jdbc.serialization

import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*

// Copy of ExecutedScript, this is aim for add the Serializable annotation
// Annotation allow to serialize/deserialize this object to/from a bson document (support json only)
@Serializable
data class ExecutedScriptDb(@SerialName("_id") @ContextualSerialization val id: String = UUID.randomUUID().toString(),
                            val name: String,
                            val checksum: String,
                            val identifier: String,
                            val executionStatus: ExecutionStatus,
                            val executionDurationInMillis: Long? = null,
                            val executionOutput: String? = null)

// Mapping function
fun ExecutedScriptDb.toExecutedScript() = ExecutedScript(
        name,
        checksum,
        identifier,
        executionStatus,
        executionDurationInMillis,
        executionOutput
)
fun ExecutedScript.toExecutedScriptDb() = ExecutedScriptDb(
        name = name,
        checksum = checksum,
        identifier = identifier,
        executionStatus = executionStatus,
        executionDurationInMillis = executionDurationInMillis,
        executionOutput = executionOutput
)

class KResultParser {
    // Mapper between json and object
    private val mapper = Json(JsonConfiguration.Stable.copy())

    override fun parseArrayOfExecutedScripts(executedScriptJsonArray: String): Sequence<ExecutedScript> {

        return mapper.parse(ExecutedScriptDb.serializer().list, executedScriptJsonArray)
                .map { it.toExecutedScript() }
                .asSequence()
    }

    override fun serializeExecutedScript(executedScript: ExecutedScript): String {
        val executedScriptDb = executedScript.toExecutedScriptDb()

        return mapper.stringify(ExecutedScriptDb.serializer(), executedScriptDb)
    }
}
