package datamaintain.db.drivers

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import datamaintain.*
import datamaintain.report.ExecutionLineReport
import datamaintain.report.ExecutionStatus
import datamaintain.report.ScriptLineReport
import org.litote.kmongo.KMongo
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant

class MongoDatamaintainDriver(
        dbName: String,
        private val tmpFilePath: Path = Paths.get("/tmp/datamaintain.tmp")
) : DatamaintainDriver {
    private val database: MongoDatabase
    private val executedScriptsCollection: MongoCollection<ScriptWithoutContent>

    companion object {
        const val EXECUTED_SCRIPTS_COLLECTION = "executed-scripts"
    }

    init {
        val client = KMongo.createClient()
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

        val result = listOf("mongo", database.name, "--quiet", scriptPath.toString()).runProcess()
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
        executedScriptsCollection.insertOne(ScriptWithoutContent(script.name, script.checksum))
    }
}
