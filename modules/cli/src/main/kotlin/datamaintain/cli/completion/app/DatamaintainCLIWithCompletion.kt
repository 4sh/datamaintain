package datamaintain.cli.completion.app

import com.github.ajalt.clikt.completion.CompletionCommand
import com.github.ajalt.clikt.core.subcommands
import datamaintain.cli.app.Datamaintain
import datamaintain.cli.app.ListExecutedScripts
import datamaintain.cli.app.update.db.MarkOneScriptAsExecuted
import datamaintain.cli.app.update.db.UpdateDb

val datamaintainWithCompletionApp = Datamaintain().subcommands(UpdateDb(), ListExecutedScripts(), MarkOneScriptAsExecuted(), CompletionCommand())

fun main(args: Array<String>) {
    datamaintainWithCompletionApp.main(args)
}