package datamaintain.cli.app

import com.github.ajalt.clikt.completion.CompletionCommand
import com.github.ajalt.clikt.core.subcommands
import datamaintain.cli.app.update.db.MarkOneScriptAsExecuted
import datamaintain.cli.app.update.db.UpdateDb

val datamaintainWithCompletionApp = App().subcommands(UpdateDb(), ListExecutedScripts(), MarkOneScriptAsExecuted(), CompletionCommand())

fun main(args: Array<String>) {
    datamaintainWithCompletionApp.main(args)
}