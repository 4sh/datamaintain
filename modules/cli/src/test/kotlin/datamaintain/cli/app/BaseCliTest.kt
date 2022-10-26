package datamaintain.cli.app

import com.github.ajalt.clikt.core.subcommands
import datamaintain.cli.app.update.db.MarkOneScriptAsExecuted
import datamaintain.cli.app.update.db.UpdateDb
import datamaintain.core.config.DatamaintainConfig

open class BaseCliTest {
    data class ConfigWrapper(var datamaintainConfig: DatamaintainConfig? = null)

    protected val configWrapper = ConfigWrapper()

    private fun runner(config: DatamaintainConfig) {
        configWrapper.datamaintainConfig = config
    }

    private fun runAppWithSubCommand(subCommand: DatamaintainCliCommand, argv: List<String>) {
        Datamaintain().subcommands(subCommand).main(argv)
    }

    protected fun runAppWithUpdateDb(baseArguments: List<String>, updateDbArguments: List<String> = listOf()) {
        runAppWithSubCommand(UpdateDb(runner = ::runner), baseArguments + "update-db" + updateDbArguments)
    }

    protected fun runAppWithMarkOneScriptAsExecuted(baseArguments: List<String>, markScriptAsExecutedArguments: List<String> = listOf()) {
        runAppWithSubCommand(
            MarkOneScriptAsExecuted(runner = ::runner),
            baseArguments + "mark-script-as-executed" + markScriptAsExecutedArguments
        )
    }
}
