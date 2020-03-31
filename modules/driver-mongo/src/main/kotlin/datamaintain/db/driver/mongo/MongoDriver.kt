package datamaintain.db.driver.mongo

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.util.runProcess
import mu.KotlinLogging
import org.bson.Document
import java.io.InputStream
import java.nio.file.Path
import kotlin.streams.asSequence

private val logger = KotlinLogging.logger {}

class MongoDriver(private val connectionString: ConnectionString,
                  private val tmpFilePath: Path,
                  private val clientPath: Path,
                  private val printOutput: Boolean,
                  private val saveOutput: Boolean
) : DatamaintainDriver {

    private val executedScriptsCollection: MongoCollection<Document>

    companion object {

        const val EXECUTED_SCRIPTS_COLLECTION = "executedScripts"

        private const val SCRIPT_DOCUMENT_NAME = "name"
        private const val SCRIPT_DOCUMENT_CHECKSUM = "checksum"
        private const val SCRIPT_DOCUMENT_IDENTIFIER = "identifier"
        private const val SCRIPT_DOCUMENT_EXECUTION_STATUS = "executionStatus"
        private const val SCRIPT_DOCUMENT_EXECUTION_OUTPUT = "executionOutput"

        fun executedScriptToDocument(executedScript: ExecutedScript): Document =
                Document().append(SCRIPT_DOCUMENT_NAME, executedScript.name)
                        .append(SCRIPT_DOCUMENT_CHECKSUM, executedScript.checksum)
                        .append(SCRIPT_DOCUMENT_IDENTIFIER, executedScript.identifier)
                        .append(SCRIPT_DOCUMENT_EXECUTION_STATUS, executedScript.executionStatus.name)
                        .append(SCRIPT_DOCUMENT_EXECUTION_OUTPUT, executedScript.executionOutput)


        fun documentToExecutedScript(document: Document) =
                ExecutedScript(
                        document.getString(SCRIPT_DOCUMENT_NAME),
                        document.getString(SCRIPT_DOCUMENT_CHECKSUM),
                        document.getString(SCRIPT_DOCUMENT_IDENTIFIER),
                        ExecutionStatus.valueOf(document.getString(SCRIPT_DOCUMENT_EXECUTION_STATUS)),
                        document.getString(SCRIPT_DOCUMENT_EXECUTION_OUTPUT)
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

        var executionOutput: String? = null

        val exitCode = listOf(clientPath.toString(), "$connectionString", "--quiet", scriptPath.toString()).runProcess() { inputStream ->
            executionOutput = processDriverOutput(inputStream)
        }

        return ExecutedScript(
                script.name,
                script.checksum,
                script.identifier,
                if (exitCode == 0) ExecutionStatus.OK else ExecutionStatus.KO,
                executionOutput
        )
    }

    private fun processDriverOutput(inputStream: InputStream): String? {
        if (saveOutput || printOutput) {
            val lines = inputStream.bufferedReader().lines().asSequence()
                    .onEach {
                        if (printOutput) {
                            logger.info { it }
                        }
                    }
                    .toList()
            if (saveOutput) {
                return lines.joinToString("\n")
            }
        }
        return null
    }

    override fun listExecutedScripts(): Sequence<ExecutedScript> {
        return executedScriptsCollection.find().asSequence().map { documentToExecutedScript(it) }
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        val executedScriptDocument = executedScriptToDocument(executedScript)
        executedScriptsCollection.insertOne(executedScriptDocument)
        return executedScript
    }
}
