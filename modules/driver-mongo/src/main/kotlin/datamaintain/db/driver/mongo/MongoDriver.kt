package datamaintain.db.driver.mongo

import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.exception.DatamaintainMongoQueryException
import datamaintain.core.script.FileScript
import datamaintain.core.step.executor.Execution
import datamaintain.core.util.runProcess
import datamaintain.db.driver.mongo.mapping.LightExecutedScriptDb
import datamaintain.db.driver.mongo.mapping.toExecutedScriptDb
import datamaintain.db.driver.mongo.mapping.toLightExecutedScript
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ExecutionStatus
import datamaintain.domain.script.LightExecutedScript
import datamaintain.domain.script.ScriptWithContent
import mu.KotlinLogging
import java.io.InputStream
import java.nio.file.Path
import kotlin.streams.asSequence
import kotlin.streams.toList

private val logger = KotlinLogging.logger {}

class MongoDriver(mongoUri: String,
                  private val tmpFilePath: Path,
                  private val clientPath: Path,
                  private val printOutput: Boolean,
                  private val saveOutput: Boolean,
                  private val mongoShell: MongoShell,
                  private val jsonMapper: JsonMapper
) : DatamaintainDriver(mongoUri) {
    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executedScripts"

        // This constant was found doing multiple tests on scripts logging too match
        const val OUTPUT_MAX_SIZE = 120000
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

        val exitCode = listOf(clientPath.toString(), uri, "--quiet", scriptPath.toString()).runProcess() { inputStream ->
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
                var dropped = 0
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

    override fun listExecutedScripts(): Sequence<LightExecutedScript> {
        val executionOutput: String = executeMongoQuery("db.$EXECUTED_SCRIPTS_COLLECTION.find({}, { \"name\": 1, \"checksum\": 1, \"identifier\": 1}).toArray()")
        return if (executionOutput.isNotBlank()) {
            (jsonMapper.fromJson(executionOutput, Array<LightExecutedScriptDb>::class.java) ?: emptyArray())
                .asSequence()
                .map { it.toLightExecutedScript() }
        } else emptySequence()
    }

    override fun markAsExecuted(executedScript: ExecutedScript): ExecutedScript {
        val executedScriptJson = jsonMapper.toJson(executedScript.toExecutedScriptDb())
        executeMongoQuery("db.$EXECUTED_SCRIPTS_COLLECTION.insert($executedScriptJson)")
        return executedScript
    }

    override fun overrideScript(executedScript: ExecutedScript): ExecutedScript {
        val set = "\$set";

        executeMongoQuery("""
            db.$EXECUTED_SCRIPTS_COLLECTION.updateOne({
                  "identifier": "${executedScript.identifier}",
                  "name": "${executedScript.name}"
                }, {
                  "$set" : {
                    "checksum" : "${executedScript.checksum}",
                    "executionStatus": "${executedScript.executionStatus.name}",
                    "action": "${executedScript.action!!.name}"
                  }
                }, {})
                """)
        return executedScript
    }

    private fun executeMongoQuery(query: String): String {
        var executionOutput: String? = null

        // If Shell is mongosh we must use stringify function for obtain a correct JSON result
        val evalQuery = if (mongoShell == MongoShell.MONGOSH) {
            "EJSON.stringify($query)"
        } else {
            query
        }

        // Execute
        val exitCode = listOf(clientPath.toString(), uri, "--quiet", "--eval", evalQuery).runProcess { inputStream ->
            var lines = inputStream.bufferedReader().lines().toList()

            // Dropwhile is a workaround to fix this issue: https://jira.mongodb.org/browse/SERVER-27159
            if (mongoShell == MongoShell.MONGO) {
                lines = lines.dropWhile { !(it.startsWith("[").or(it.startsWith("{"))) }
            }

            executionOutput = lines.joinToString("\n")
        }

        if (exitCode != 0 || executionOutput == null) {
            throw DatamaintainMongoQueryException(query, exitCode, executionOutput)
        }

        return executionOutput as String
    }
}
