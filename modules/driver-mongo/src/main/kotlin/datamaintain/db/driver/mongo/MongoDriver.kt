package datamaintain.db.driver.mongo

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
import org.litote.kmongo.KMongo
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant


class MongoDriver(dbName: String,
                  private val mongoUri: String,
                  private val tmpFilePath: Path = Paths.get(MongoConfigKey.DB_MONGO_TMP_PATH.default!!)
) : DatamaintainDriver {
    private val database: MongoDatabase
    private val executedScriptsCollection: MongoCollection<ScriptWithoutContent>

    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executed-scripts"
    }

    init {
        val client = KMongo.createClient(mongoUri)
        database = client.getDatabase(dbName)
        executedScriptsCollection = database.getCollection(EXECUTED_SCRIPTS_COLLECTION, ScriptWithoutContent::class.java)
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

        val result = listOf("mongo", "--host", mongoUri, database.name, "--quiet", scriptPath.toString()).runProcess()
        return ExecutionLineReport(
                Instant.now(),
                result.output,
                if (result.exitCode == 0) ExecutionStatus.OK else ExecutionStatus.KO,
                script
        )
    }

    override fun listExecutedScripts(): Sequence<Script> {
        return executedScriptsCollection.find().asSequence()
    }

    override fun markAsExecuted(script: Script) {
        executedScriptsCollection.insertOne(ScriptWithoutContent(script.name, script.checksum, script.identifier))
    }
}
