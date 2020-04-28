package datamaintain.db.driver.mongo

import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.util.runProcess
import mu.KotlinLogging
import java.io.InputStream
import java.lang.IllegalStateException
import java.nio.file.Path
import kotlin.streams.asSequence
import kotlin.streams.toList

private val logger = KotlinLogging.logger {}

class MongoDriver(private val mongoUri: String,
                  private val tmpFilePath: Path,
                  private val clientPath: Path,
                  private val printOutput: Boolean,
                  private val saveOutput: Boolean
) : DatamaintainDriver {
    private val bsonParser = KBsonParser()

    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executedScripts"
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

        val exitCode = listOf(clientPath.toString(), mongoUri, "--quiet", scriptPath.toString()).runProcess() { inputStream ->
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
        val executionOutput: String = executeMongoQuery("db.$EXECUTED_SCRIPTS_COLLECTION.find().toArray()")
        return if (executionOutput.isNotBlank()) bsonParser.parseArrayOfExecutedScripts(executionOutput) else emptySequence()
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        val executedScriptBson = bsonParser.serializeExecutedScript(executedScript)
        executeMongoQuery("db.$EXECUTED_SCRIPTS_COLLECTION.insert($executedScriptBson)")
        return executedScript
    }

    private fun executeMongoQuery(query: String): String {
        var executionOutput: String? = null
        val exitCode = listOf(clientPath.toString(), mongoUri, "--quiet", "--eval", query).runProcess { inputStream ->
            val lines = inputStream.bufferedReader().lines().toList()
            executionOutput = lines.joinToString("\n")
        }

        if (exitCode != 0 || executionOutput == null) {
            throw IllegalStateException("Query $query fail with exit code $exitCode an output : $executionOutput")
        }

        return executionOutput as String
    }
}
