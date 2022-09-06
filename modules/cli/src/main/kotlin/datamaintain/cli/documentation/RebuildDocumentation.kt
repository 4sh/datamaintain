package datamaintain.cli.documentation

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.PrintHelpMessage
import datamaintain.cli.app.datamaintainApp
import java.io.File
import java.io.OutputStreamWriter

fun main() {
    writeDocumentation(File("../../docs/cli-configuration.md").outputStream().writer())
}

fun writeDocumentation(writer: OutputStreamWriter) {
    writer.append(getMarkdownHelp())
    writer.append("# Subcommands\n")
    datamaintainApp.registeredSubcommandNames().forEach {
        writer.append("## $it\n")
        writer.append(getMarkdownHelp(listOf(it), "### Options\n"))
    }
    writer.flush()
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
