package datamaintain.cli.app.update.db

import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DriverConfigKey
import datamaintain.core.script.ScriptAction
import datamaintain.core.step.check.allCheckRuleNames
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.db.driver.mongo.MongoConfigKey
import datamaintain.db.driver.mongo.MongoShell
import java.util.*

class UpdateDb(runner: (DatamaintainConfig) -> Unit = ::defaultUpdateDbRunner) : DatamaintainCliUpdateDbCommand(
    name = "update-db",
    runner = runner
) {
    private val path: String? by option(help = "path to directory containing scripts")

    private val identifierRegex: String? by option(help = "regex to extract identifier part from scripts")

    private val whitelistedTags: String? by option(help = "tags to whitelist (separated by ','")

    private val blacklistedTags: String? by option(help = "tags to blacklist (separated by ',')")

    private val tagsToPlayAgain: String? by option(help = "tags to play again at each datamaintain execution (separated by ',')")

    private val createTagsFromFolder: Boolean? by option(help = "create automatically tags from parent folders").flag()

    private val executionMode by option(help = "execution mode").choice(ExecutionMode.values().map { it.name }.map { it to it }.toMap())

    private val action by option(help = "script action").choice(ScriptAction.values().map { it.name }.map { it to it }.toMap())

    private val allowAutoOverride: Boolean? by option(help = "Allow datamaintain to automaticaly override scripts").flag()

    private val verbose: Boolean? by option(help = "verbose").flag()

    private val saveDbOutput: Boolean? by option(help = "save your script and db output").flag()

    private val printDbOutput: Boolean? by option(help = "print your script and db output").flag()

    private val tagsMatchers: List<Pair<String, String>>? by option("--tag", help = "Tag defined using glob path matchers. " +
            "To define multiple tags, use option multiple times. " +
            "Syntax example: MYTAG1=[pathMatcher1, pathMatcher2]")
        .convert {
            val split = it.split("=")
            Pair(split[0], split[1])
        }
        .multiple()

    private val checkRules: List<String>? by option("--rule", help = "check rule to play. " +
            "To define multiple rules, use option multiple times.")
        .choice(allCheckRuleNames.map { it to it }.toMap())
        .multiple()

    private val mongoShell: String? by option(help = "mongo binary, can be mongo or mongosh. mongo by default")
        .choice(MongoShell.values().map { it.name }.map { it.toLowerCase() }.associateWith { it })

    private val porcelain: Boolean? by option(help = "for each executed script, display relative path to scan path").flag()

    private val flags: List<String>? by option(help = "add a flag on the executed scripts. " +
            "To define multiple rules, use option multiple times.")
        .multiple()

    override fun overloadProps(props: Properties) {
        path?.let { props.put(CoreConfigKey.SCAN_PATH.key, it) }
        identifierRegex?.let { props.put(CoreConfigKey.SCAN_IDENTIFIER_REGEX.key, it) }
        whitelistedTags?.let { props.put(CoreConfigKey.TAGS_WHITELISTED.key, it) }
        blacklistedTags?.let { props.put(CoreConfigKey.TAGS_BLACKLISTED.key, it) }
        tagsToPlayAgain?.let { props.put(CoreConfigKey.PRUNE_TAGS_TO_RUN_AGAIN.key, it) }
        createTagsFromFolder?.let { props.put(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.key, it.toString()) }
        verbose?.let { props.put(CoreConfigKey.VERBOSE.key, it.toString()) }
        saveDbOutput?.let { props.put(DriverConfigKey.DB_SAVE_OUTPUT.key, it.toString()) }
        printDbOutput?.let { props.put(DriverConfigKey.DB_PRINT_OUTPUT.key, it.toString()) }
        executionMode?.let { props.put(CoreConfigKey.EXECUTION_MODE.key, it) }
        action?.let { props.put(CoreConfigKey.DEFAULT_SCRIPT_ACTION.key, it) }
        tagsMatchers?.forEach {
            props.put("${CoreConfigKey.TAG.key}.${it.first}", it.second)
        }
        checkRules?.let { props.put(CoreConfigKey.CHECK_RULES.key, it.optionListToString()) }
        allowAutoOverride?.let { props.put(CoreConfigKey.PRUNE_OVERRIDE_UPDATED_SCRIPTS.key, it.toString()) }
        porcelain?.let { props.put(CoreConfigKey.PRINT_RELATIVE_PATH_OF_SCRIPT.key, it.toString()) }
        mongoShell?.let { props.put(MongoConfigKey.DB_MONGO_SHELL.key, it.toUpperCase()) }
        flags?.let { props.put(CoreConfigKey.FLAGS.key, it.optionListToString()) }
    }

    fun List<String>.optionListToString(): String = this.joinToString(",")
}
