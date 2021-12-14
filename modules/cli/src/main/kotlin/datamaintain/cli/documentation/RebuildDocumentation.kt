package datamaintain.cli.documentation

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.PrintHelpMessage
import datamaintain.cli.app.datamaintainApp
import java.io.File

fun main() {
    File("../../docs/cli-configuration.md").writeText(buildDocumentation())
}

fun buildDocumentation(): String {
    return buildString {
        append(getMarkdownHelp())
        append("# Subcommands\n")
        datamaintainApp.registeredSubcommandNames().forEach {
            append("## $it\n")
            append(getMarkdownHelp(listOf(it), "### Options\n"))
        }
    }
}

fun getMarkdownHelp(commands: List<String> = listOf(), optionsTitle: String = "# Options"): String {
    try {
        datamaintainApp.parse(
            commands +  "--help",
            Context.build(datamaintainApp) { helpFormatter = MarkdownHelpFormatter(optionsTitle) })
    } catch (e: PrintHelpMessage) {
        return e.command.getFormattedHelp()
    }

    return ""
}
