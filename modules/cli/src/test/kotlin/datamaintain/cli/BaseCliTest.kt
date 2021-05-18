package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.cli.update.db.MarkOneScriptAsExecuted
import datamaintain.cli.update.db.UpdateDb
import datamaintain.core.config.DatamaintainConfig

open class BaseCliTest {
    data class ConfigWrapper(var datamaintainConfig: DatamaintainConfig? = null)

    protected val configWrapper = ConfigWrapper()

    private fun runner(config: DatamaintainConfig) {
        configWrapper.datamaintainConfig = config
    }

    protected fun runApp(argv: List<String>) {
        App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts(), MarkOneScriptAsExecuted(runner = ::runner)).main(argv)
    }
}