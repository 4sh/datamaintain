package datamaintain.db.drivers

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import datamaintain.Script
import datamaintain.ScriptWithContent
import datamaintain.ScriptWithoutContent
import org.litote.kmongo.KMongo

class MongoDatamaintainDriver(dbName: String) : DatamaintainDriver {
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

    override fun executeScript(script: ScriptWithContent): ScriptExecutionReport {
        throw NotImplementedError("MongoDatamaintainDriver executeScript method should not be used")
    }

    override fun listExecutedScripts(): Sequence<Script> {
        return executedScriptsCollection.find().asSequence()
    }

    override fun markAsExecuted(script: Script) {
        executedScriptsCollection.insertOne(ScriptWithoutContent(script.name, script.checksum))
    }
}