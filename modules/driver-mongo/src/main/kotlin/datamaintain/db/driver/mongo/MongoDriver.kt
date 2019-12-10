package datamaintain.db.driver.mongo

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.report.ExecutionLineReport
import datamaintain.core.report.ExecutionStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.Script
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.ScriptWithoutContent
import datamaintain.core.util.runProcess
import org.bson.Document
import java.nio.file.Path
import java.time.Instant

class MongoDriver(dbName: String,
                  private val mongoUri: String,
                  private val tmpFilePath: Path
) : DatamaintainDriver {
    private val database: MongoDatabase
    private val executedScriptsCollection: MongoCollection<Document>

    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executed-scripts"
    }

    init {
        val client = MongoClients.create("mongodb://$mongoUri")
        database = client.getDatabase(dbName)
        executedScriptsCollection = database.getCollection(EXECUTED_SCRIPTS_COLLECTION, Document::class.java)
    }

    override fun executeScript(script: ScriptWithContent): ExecutionLineReport {
        // $eval mongo command is not available after driver 4.0, so we execute script via an external process

        val scriptPath = when (script) {
            is FileScript -> script.path
            else -> {
                tmpFilePath.toFile().writeText(script.content)
                tmpFilePath
            }
        }

        val result = listOf("mongo", "mongodb://$mongoUri/${database.name}", "--quiet", scriptPath.toString()).runProcess()
        return ExecutionLineReport(
                Instant.now(),
                result.output,
                if (result.exitCode == 0) ExecutionStatus.OK else ExecutionStatus.KO,
                script
        )
    }

    override fun listExecutedScripts(): Sequence<Script> {
        return executedScriptsCollection.find().asSequence().map { documentToScriptWithoutContent(it) }
    }

    override fun markAsExecuted(script: Script) {
        val scriptWithoutContent = ScriptWithoutContent(script.name, script.checksum, script.identifier)
        val scriptWithoutContentDocument = scriptWithoutContentToDocument(scriptWithoutContent)
        executedScriptsCollection.insertOne(scriptWithoutContentDocument)
    }

    private fun scriptWithoutContentToDocument(script: ScriptWithoutContent): Document {
        return Document()
                .append("name", script.name)
                .append("checksum", script.checksum)
                .append("identifier", script.identifier)
    }

    private fun documentToScriptWithoutContent(document: Document): ScriptWithoutContent {
        val name: String = document.getString("name")
        val checksum: String = document.getString("checksum")
        val identifier: String = document.getString("identifier")
        return ScriptWithoutContent(name, checksum, identifier)
    }
}
