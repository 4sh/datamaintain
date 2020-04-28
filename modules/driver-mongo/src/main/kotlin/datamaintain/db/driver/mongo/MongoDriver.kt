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

private val logger = KotlinLogging.logger {}

class MongoDriver(private val mongoUri: String,
                  private val tmpFilePath: Path,
                  private val clientPath: Path,
                  private val printOutput: Boolean,
                  private val saveOutput: Boolean
) : DatamaintainDriver {
    val jsonParser = JsonParser();

    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executedScripts"
    }

    init {
        // TODO check URI ?
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

    override fun listExecutedScripts(): List<ExecutedScript> {
        var executionOutput: String? = null
        val exitCode = listOf(clientPath.toString(), mongoUri, "--quiet", "--eval", "db.$EXECUTED_SCRIPTS_COLLECTION.findOne()").runProcess() { inputStream ->
            val lines = inputStream.bufferedReader().lines().asSequence()
                    .toList();

            executionOutput = lines.joinToString("\n")
        }

        if (exitCode != 0 || executionOutput == null) {
            throw IllegalStateException();
        }

        if (executionOutput!!.isNotBlank()) {
            return jsonParser.parseExecutedScripts(executionOutput!!)
        }

        return listOf();
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
//        val executedScriptDocument = executedScriptToDocument(executedScript)
//        executedScriptsCollection.insertOne(executedScriptDocument)
        return executedScript
    }
}
