package datamaintain.cli.app.utils

import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.option

const val examplesHelpKey = "examples"
const val defaultHelpKey = "default"

fun ParameterHolder.detailedOption(
    vararg names: String,
    help: String = "",
    metavar: String? = null,
    hidden: Boolean = false,
    envvar: String? = null,
    helpTags: Map<String, String> = emptyMap(),
    example: String? = null,
    defaultValue: String? = null
): RawOption {
    var detailedHelpTags = helpTags

    fun addValueToHelpTagsIfNotNull (key: String, value: String?) {
        if(value != null) {
            detailedHelpTags = detailedHelpTags.plus(key to value)
        }
    }

    addValueToHelpTagsIfNotNull(examplesHelpKey, example)
    addValueToHelpTagsIfNotNull(defaultHelpKey, defaultValue)

    return this.option(
        names = *names,
        help = help,
        metavar = metavar,
        hidden = hidden,
        envvar = envvar,
        helpTags = detailedHelpTags
    )
}