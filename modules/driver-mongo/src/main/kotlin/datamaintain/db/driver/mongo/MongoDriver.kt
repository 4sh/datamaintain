package datamaintain.db.driver.mongo

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.script.*
import datamaintain.core.util.runProcess
import org.bson.Document
import java.nio.file.Path
import kotlin.streams.toList

class MongoDriver(private val connectionString: ConnectionString,
                  private val tmpFilePath: Path,
                  private val clientPath: Path
) : DatamaintainDriver {

    private val executedScriptsCollection: MongoCollection<Document>

    companion object {

        const val EXECUTED_SCRIPTS_COLLECTION = "executed-scripts"

        private const val SCRIPT_DOCUMENT_NAME = "name"
        private const val SCRIPT_DOCUMENT_CHECKSUM = "checksum"
        private const val SCRIPT_DOCUMENT_IDENTIFIER = "identifier"
        private const val SCRIPT_DOCUMENT_EXECUTION_STATUS = "executionStatus"
        private const val SCRIPT_DOCUMENT_MARK_AS_EXECUTED_FORCED = "markAsExecutedForced"
        private const val SCRIPT_DOCUMENT_EXECUTION_OUTPUT = "executionOutput"

        fun executedScriptToDocument(executedScript: ExecutedScript): Document =
                Document().append(MongoDriver.SCRIPT_DOCUMENT_NAME, executedScript.name)
                        .append(MongoDriver.SCRIPT_DOCUMENT_CHECKSUM, executedScript.checksum)
                        .append(MongoDriver.SCRIPT_DOCUMENT_IDENTIFIER, executedScript.identifier)
                        .append(MongoDriver.SCRIPT_DOCUMENT_EXECUTION_STATUS, executedScript.executionStatus)
                        .append(MongoDriver.SCRIPT_DOCUMENT_MARK_AS_EXECUTED_FORCED, executedScript.markAsExecutedForced)
                        .append(MongoDriver.SCRIPT_DOCUMENT_EXECUTION_OUTPUT, executedScript.executionOutput)


        fun documentToExecutedScript(document: Document) =
                ExecutedScript(
                        document.getString(MongoDriver.SCRIPT_DOCUMENT_NAME),
                        document.getString(MongoDriver.SCRIPT_DOCUMENT_CHECKSUM),
                        document.getString(MongoDriver.SCRIPT_DOCUMENT_IDENTIFIER),
                        ExecutionStatus.valueOf(document.getString(MongoDriver.SCRIPT_DOCUMENT_EXECUTION_STATUS)),
                        document.getBoolean(MongoDriver.SCRIPT_DOCUMENT_MARK_AS_EXECUTED_FORCED),
                        document.getString(MongoDriver.SCRIPT_DOCUMENT_EXECUTION_OUTPUT)
                )

    }

    init {
        val client = MongoClients.create(this.connectionString)
        val database: MongoDatabase = client.getDatabase(this.connectionString.database!!)
        executedScriptsCollection = database.getCollection(EXECUTED_SCRIPTS_COLLECTION, Document::class.java)
    }

    override fun executeScript(script: ScriptWithContent): ExecutedScript {
        // $eval mongo command is not available after driver 4.0, so we execute script via an external process

        val scriptPath = when (script) {
            is FileScript -> script.path
            else -> {
                tmpFilePath.toFile().writeText(script.content)
                tmpFilePath
            }
        }

        // TODO handle output option
        val saveOutput = false
        var executionOutput: String? = null

        val exitCode = listOf(clientPath.toString(), "$connectionString", "--quiet", scriptPath.toString()).runProcess() { inputStream ->
            if (saveOutput) {
                executionOutput = inputStream.bufferedReader().lines().toList().joinToString("\n")
            }
        }

        return ExecutedScript(
                script.name,
                script.checksum,
                script.identifier,
                if (exitCode == 0) ExecutionStatus.OK else ExecutionStatus.KO,
                executionOutput
        )
    }

    override fun listExecutedScripts(): Sequence<Script> {
        return executedScriptsCollection.find().asSequence().map { documentToExecutedScript(it) }
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        val executedScriptDocument = executedScriptToDocument(executedScript)
        executedScriptsCollection.insertOne(executedScriptDocument)
        return executedScript;
    }
}
