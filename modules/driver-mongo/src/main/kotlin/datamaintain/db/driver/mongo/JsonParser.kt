package datamaintain.db.driver.mongo

import com.github.jershell.kbson.KBson
import com.github.jershell.kbson.ObjectIdSerializer
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.bson.BsonDocument
import org.bson.types.ObjectId

@Serializable
data class Data(@SerialName("_id") @ContextualSerialization val id: ObjectId = ObjectId(),
                val name: String,
                val checksum: String,
                val identifier: String,
                val executionStatus: ExecutionStatus,
                val executionOutput: String? = null)

class JsonParser {
    private val mapper = Json(JsonConfiguration.Stable)

    fun parseExecutedScripts(listExecutedScriptJson: String): List<ExecutedScript> {

        val parse = KBson().parse(Data.serializer(), BsonDocument.parse(listExecutedScriptJson))

        print(parse)
        return listOf();
    }


}

fun main(args: Array<String>) {
    JsonParser().parseExecutedScripts("""
        {
        	"_id" : ObjectId("5e962c5acbbc757328a67541"),
        	"name" : "script1.js",
        	"checksum" : "c4ca4238a0b923820dcc509a6f75849b",
        	"identifier" : "",
        	"executionStatus" : "OK",
        	"executionOutput" : null
        }
    """.trimIndent())
}
