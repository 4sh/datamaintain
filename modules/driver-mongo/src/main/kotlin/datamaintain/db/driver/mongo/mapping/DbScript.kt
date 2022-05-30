package datamaintain.db.driver.mongo.mapping

import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ScriptAction
import java.util.*

typealias MongoId = String

fun generateMongoId() = UUID.randomUUID().toString()

/**
 * Copy of LightExecutedScript, add _id object
 *
 * _id is an unconventional name
 * We use _id over id for avoid duplicated code in serializer (like @PropertyName in Jackson and Gson)
 *
 * We use default value and an empty constructor for avoid duplicated code in serializer (Jackson and Kotlinx)
 */
open class LightExecutedScriptDb(
    open var _id: MongoId = generateMongoId(),
    open var name: String? = null,
    open var checksum: String? = null,
    open var identifier: String? = null
) {
    constructor() : this(generateMongoId())
}

/**
 * Copy of ExecutedScript, add _id object
 *
 * _id is an unconventional name
 * We use _id over id for avoid duplicated code in serializer (like @PropertyName in Jackson and Gson)
 *
 * We use default value and an empty constructor for avoid duplicated code in serializer (Jackson)
 */
open class ExecutedScriptDb(
    open var _id: MongoId = generateMongoId(),
    open var name: String? = null,
    open var checksum: String? = null,
    open var identifier: String? = null,
    open var executionStatus: ExecutionStatus? = null,
    open var action: ScriptAction? = null,
    open var executionDurationInMillis: Long? = null,
    open var executionOutput: String? = null,
    open var flags: List<String> = listOf()
) {
    constructor() : this(generateMongoId())
}
