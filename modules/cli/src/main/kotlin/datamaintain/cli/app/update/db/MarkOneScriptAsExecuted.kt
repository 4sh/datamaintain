package datamaintain.cli.app.update.db

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import datamaintain.cli.app.utils.detailedOption
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.ScriptAction
import java.util.*

class MarkOneScriptAsExecuted(runner: (DatamaintainConfig) -> Unit = ::defaultUpdateDbRunner) :
    DatamaintainCliUpdateDbCommand(
        name = "mark-script-as-executed",
        runner = runner,
        help = "Mark one specified script as executed"
    ) {
    private val path: String? by detailedOption(
        help = "path to the script you want to mark as executed",
        example = "scripts/myScript1.js",
        defaultValue = CoreConfigKey.SCAN_PATH.default
    )

    private val verbose: Boolean? by detailedOption(
        help = "verbose",
        defaultValue = CoreConfigKey.VERBOSE.default
    ).flag()

    override fun overloadProps(props: Properties) {
        props[CoreConfigKey.DEFAULT_SCRIPT_ACTION.key] = ScriptAction.MARK_AS_EXECUTED.name

        // Overload from arguments
        path?.let { props.put(CoreConfigKey.SCAN_PATH.key, it) }
        verbose?.let { props.put(CoreConfigKey.VERBOSE.key, it.toString()) }
    }
}