package datamaintain.cli.documentation

import com.github.ajalt.clikt.output.HelpFormatter
import datamaintain.cli.app.utils.defaultHelpKey
import datamaintain.cli.app.utils.examplesHelpKey

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
        row(
            "Names",
            "Default value",
            "Needs argument",
            "Possible arguments",
            "Description",
            "Examples"
        )
        append("|---|---|---|---|---|---|\n")
        options.forEach {
            row(
                it.names.joinToString(", "),
                it.defaultValue(),
                it.waitingForValue(),
                it.possibleValues(),
                it.customizedHelp(),
                it.examples()
            )
        }
    }

    private fun StringBuilder.row(vararg columns: String?) = append(columns.joinToString("|", "|", "|\n"))
}

private fun HelpFormatter.ParameterHelp.Option.defaultValue(): String = tags[defaultHelpKey] ?: "No default value"

private fun HelpFormatter.ParameterHelp.Option.waitingForValue(): String {
    return if(this.metavar == null) "✘" else "✔"
}

private fun HelpFormatter.ParameterHelp.Option.possibleValues(): String {
    val metavar = this.metavar ?: return "N/A"

    if(metavar.contains("[")) {
        // Metavar contains list of available values
        return metavar.substring(1, metavar.length - 1)
            .split("|")
            .joinToString(" or ") { it.formatToMarkdownCode() }
    }

    if(metavar == "VALUE") {
        return " "
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

private fun String.formatToMarkdownCode(): String = "```$this```"

private fun HelpFormatter.ParameterHelp.Option.examples(): String {
    val examples = this.tags[examplesHelpKey] ?: return ""
    return examples.split(";").joinToString(", ") { it.formatToMarkdownCode() }
}