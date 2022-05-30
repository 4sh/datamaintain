package datamaintain.db.driver.mongo.serialization

import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ScriptAction
import datamaintain.db.driver.mongo.mapping.ExecutedScriptDb
import datamaintain.db.driver.mongo.mapping.LightExecutedScriptDb
import datamaintain.db.driver.mongo.mapping.MongoId
import datamaintain.db.driver.mongo.mapping.generateMongoId
import kotlinx.serialization.Serializable

// Copy of DB object for add Serializable annotation
@Serializable
class SerializationLightExecutedScriptDb(
    var _id: MongoId = generateMongoId(),
    var name: String? = null,
    var checksum: String? = null,
    var identifier: String? = null
) {
    fun toLightExecutedScriptDb() = LightExecutedScriptDb(
        this._id,
        this.name,
        this.checksum,
        this.identifier
    )
}

// Copy of ExecutedScript, this is aim for add the Serializable annotation
@Serializable
class SerializationExecutedScriptDb(
    override var _id: MongoId = generateMongoId(),
    override var name: String? = null,
    override var checksum: String? = null,
    override var identifier: String? = null,
    override var executionStatus: ExecutionStatus? = null,
    override var action: ScriptAction? = null,
    override var executionDurationInMillis: Long? = null,
    override var executionOutput: String? = null,
    override var flags: List<String> = listOf()
): ExecutedScriptDb(_id, name, checksum, identifier, executionStatus, action, executionDurationInMillis, executionOutput, flags)

fun ExecutedScriptDb.toSerializationExecutedScriptDb() = SerializationExecutedScriptDb(
    this._id,
    this.name,
    this.checksum,
    this.identifier,
    this.executionStatus,
    this.action,
    this.executionDurationInMillis,
    this.executionOutput,
    this.flags
)
