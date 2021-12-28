package datamaintain.cli.documentation

import com.github.ajalt.clikt.output.HelpFormatter

class MarkdownHelpFormatter(private val optionsTitle: String) : HelpFormatter {
    override fun formatHelp(
        prolog: String,
        epilog: String,
        parameters: List<HelpFormatter.ParameterHelp>,
        programName: String
    ) = buildString {
        addOptions(parameters.filterIsInstance<HelpFormatter.ParameterHelp.Option>())
    }


    override fun formatUsage(parameters: List<HelpFormatter.ParameterHelp>, programName: String): String {
        TODO("Not yet implemented")
    }

    private fun StringBuilder.addOptions(options: List<HelpFormatter.ParameterHelp.Option>) {
        append("$optionsTitle\n")
        row("Names", "Secondary names", "Possible values", "Description")
        append("|---|---|---|---|\n")
        options.forEach {
            row(
                it.names.joinToString(", "),
                it.secondaryNames.takeUnless { it.isEmpty() }?.joinToString(", ") ?: " ",
                it.possibleValues(),
                it.customizedHelp()
            )
        }
    }

    private fun StringBuilder.row(vararg columns: String?) = append(columns.joinToString("|", "|", "|\n"))
}

private fun HelpFormatter.ParameterHelp.Option.possibleValues(): String {
    val metavar = this.metavar ?: " "

    if(metavar.contains("[")) {
        // Metavar contains list of available values
        return metavar.substring(1, metavar.length - 1).split("|").map { "```$it```" }.joinToString(" or ")
    }

    return metavar
}

const val helpCommandDescription = "Display command help and exit"

private fun HelpFormatter.ParameterHelp.Option.customizedHelp(): String {
    if(this.names.contains("-h")) {
        return helpCommandDescription
    }

    return this.help
}