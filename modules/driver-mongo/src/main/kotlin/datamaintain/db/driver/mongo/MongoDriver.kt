package datamaintain.db.driver.mongo

import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.script.ExecutedScript
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.executor.Execution
import datamaintain.core.util.runProcess
import datamaintain.db.driver.mongo.serialization.KJsonParser
import mu.KotlinLogging
import java.io.InputStream
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
    private val jsonParser = KJsonParser()

    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executedScripts"

        // This constant was found doing multiple tests on scripts logging too match
        const val OUTPUT_MAX_SIZE = 150000
        const val OUTPUT_TRUNCATED_MESSAGE = "... output was truncated because it was too long"
    }

    override fun executeScript(script: ScriptWithContent): Execution {
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

        return Execution(if (exitCode == 0) ExecutionStatus.OK else ExecutionStatus.KO, executionOutput)
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
                val totalOutputSize = lines.map { line -> line.length }.foldRight(0, Int::plus)
                var dropped = 0;
                return lines
                        .dropLastWhile { line ->
                            val drop = totalOutputSize - dropped > OUTPUT_MAX_SIZE
                            if(drop) {
                                dropped += line.length
                            }
                            drop
                        }
                        .joinToString("\n")
                        .plus(if(dropped > 0) OUTPUT_TRUNCATED_MESSAGE else "")
            }
        }
        return null
    }

    override fun listExecutedScripts(): Sequence<ExecutedScript> {
        val executionOutput: String = executeMongoQuery("db.$EXECUTED_SCRIPTS_COLLECTION.find().toArray()")
        return if (executionOutput.isNotBlank()) jsonParser.parseArrayOfExecutedScripts(executionOutput) else emptySequence()
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        val executedScriptBson = jsonParser.serializeExecutedScript(executedScript)
        executeMongoQuery("db.$EXECUTED_SCRIPTS_COLLECTION.insert($executedScriptBson)")
        return executedScript
    }

    private fun executeMongoQuery(query: String): String {
        var executionOutput: String? = null
        val exitCode = listOf(clientPath.toString(), mongoUri, "--quiet", "--eval", query).runProcess { inputStream ->
            // Dropwhile is a workaround to fix this issue: https://jira.mongodb.org/browse/SERVER-27159
            val lines = inputStream.bufferedReader().lines().toList().dropWhile { !(it.startsWith("[").or(it.startsWith("{"))) }

            executionOutput = lines.joinToString("\n")
        }

        if (exitCode != 0 || executionOutput == null) {
            throw IllegalStateException("Query $query fail with exit code $exitCode an output : $executionOutput")
        }

        return executionOutput as String
    }
}
