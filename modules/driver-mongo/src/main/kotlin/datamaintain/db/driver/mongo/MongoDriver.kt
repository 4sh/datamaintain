package datamaintain.db.driver.mongo

import com.mongodb.ConnectionString
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
import kotlin.IllegalArgumentException

class MongoDriver(mongoUri: String,
                  private val tmpFilePath: Path,
                  private val clientPath: Path
) : DatamaintainDriver {
    private val mongoUri: ConnectionString = ConnectionString(mongoUri)
    private val executedScriptsCollection: MongoCollection<Document>

    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executed-scripts"
        private const val SCRIPT_DOCUMENT_NAME = "name"
        private const val SCRIPT_DOCUMENT_CHECKSUM = "checksum"
        private const val SCRIPT_DOCUMENT_IDENTIFIER = "identifier"
    }

    init {
        // mongoUri can come with a database but currently driver's dbName is mandatory
        if (this.mongoUri.database == null) {
            throw IllegalArgumentException("MongoUri does not contains a database name")
        }

        // mongoUri can come with a collection. It has no sense in DataMaintain's logic
        if (this.mongoUri.collection != null) {
            throw IllegalArgumentException("MongoUri contains a collection name, please remove it")
        }

        val client = MongoClients.create(this.mongoUri)
        val database: MongoDatabase = client.getDatabase(this.mongoUri.database!!)
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

        val result = listOf(clientPath.toString(), "$mongoUri", "--quiet", scriptPath.toString()).runProcess()
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

    fun scriptWithoutContentToDocument(script: ScriptWithoutContent): Document {
        return Document()
                .append(SCRIPT_DOCUMENT_NAME, script.name)
                .append(SCRIPT_DOCUMENT_CHECKSUM, script.checksum)
                .append(SCRIPT_DOCUMENT_IDENTIFIER, script.identifier)
    }

    fun documentToScriptWithoutContent(document: Document): ScriptWithoutContent {
        val name: String = document.getString(SCRIPT_DOCUMENT_NAME)
        val checksum: String = document.getString(SCRIPT_DOCUMENT_CHECKSUM)
        val identifier: String = document.getString(SCRIPT_DOCUMENT_IDENTIFIER)
        return ScriptWithoutContent(name, checksum, identifier)
    }
}