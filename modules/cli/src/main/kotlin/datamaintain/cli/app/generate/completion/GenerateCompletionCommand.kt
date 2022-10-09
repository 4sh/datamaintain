package datamaintain.cli.app.generate.completion

import com.github.ajalt.clikt.completion.completionOption
import datamaintain.cli.app.DatamaintainCliCommand
import datamaintain.core.config.DatamaintainConfig
import java.util.*

class GenerateCompletionCommand() : DatamaintainCliCommand(
        name = "generate-completion",
        help = "Generate completion script for Bash / Zsh"
) {

    init {
        completionOption()
    }

    override fun overloadProps(props: Properties) {

    }

    override fun executeCommand(config: DatamaintainConfig) {

    }
}
