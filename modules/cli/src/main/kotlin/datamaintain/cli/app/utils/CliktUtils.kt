package datamaintain.cli.app.utils

import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.option

const val examplesHelpKey = "examples"

fun ParameterHolder.optionWithExamples(
    vararg names: String,
    help: String = "",
    metavar: String? = null,
    hidden: Boolean = false,
    envvar: String? = null,
    envvarSplit: Regex? = null,
    helpTags: Map<String, String> = emptyMap(),
    examples: List<String> = listOf()
): RawOption = this.option(
    names = *names,
    help = help,
    metavar = metavar,
    hidden = hidden,
    envvar = envvar,
    envvarSplit = envvarSplit,
    helpTags = helpTags.plus(examplesHelpKey to examples.joinToString(", "))
)

fun ParameterHolder.optionWithExample(
    vararg names: String,
    help: String = "",
    metavar: String? = null,
    hidden: Boolean = false,
    envvar: String? = null,
    envvarSplit: Regex? = null,
    helpTags: Map<String, String> = emptyMap(),
    example: String? = null
): RawOption = this.option(
    names = *names,
    help = help,
    metavar = metavar,
    hidden = hidden,
    envvar = envvar,
    envvarSplit = envvarSplit,
    helpTags = if(example != null) helpTags.plus(examplesHelpKey to example) else helpTags
)