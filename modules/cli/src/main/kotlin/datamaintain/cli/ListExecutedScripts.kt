package datamaintain.cli

import datamaintain.cli.utils.logger
import datamaintain.core.Datamaintain
import datamaintain.core.config.DatamaintainConfig
import java.util.*

class ListExecutedScripts : DatamaintainCliCommand(name = "list") {
    override fun overloadProps(props: Properties) {

    }

    override fun executeCommand(config: DatamaintainConfig) {
        Datamaintain(config).listExecutedScripts().forEach {
            logger.info { "${it.name} (${it.checksum})" }
        }
    }
}
