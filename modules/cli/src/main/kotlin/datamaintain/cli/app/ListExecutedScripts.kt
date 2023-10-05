package datamaintain.cli.app

import datamaintain.cli.app.utils.detailedOption
import datamaintain.cli.app.utils.logger
import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import datamaintain.db.driver.mongo.MongoConfigKey
import java.util.*

class ListExecutedScripts : DatamaintainCliCommand(name = "list") {
    private val mongoClient: String? by detailedOption(
        help = "mongo binary path. The path must match --mongo-shell value.",
        example = "/path/to/mongo"
    )

    override fun overloadProps(props: Properties) {
        mongoClient?.let { props.put(MongoConfigKey.DB_MONGO_CLIENT_PATH.key, mongoClient) }
    }

    override fun executeCommand(config: DatamaintainConfig, porcelain: Boolean) {
        Datamaintain(config).listExecutedScripts().forEach {
            echo("${it.name} (${it.checksum})")
        }
    }
}
