package datamaintain.cli.app.update.db

import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import datamaintain.cli.app.utils.detailedOption
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.db.driver.DriverConfigKey
import datamaintain.core.step.check.allCheckRuleNames
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.db.driver.mongo.MongoConfigKey
import datamaintain.db.driver.mongo.MongoShell
import datamaintain.domain.script.ScriptAction
import java.util.*

class UpdateDb(runner: (DatamaintainConfig) -> Unit = ::defaultUpdateDbRunner) : DatamaintainCliUpdateDbCommand(
    name = "update-db",
    runner = runner
) {
    private val path: String? by detailedOption(
        help = "path to directory containing scripts",
        example = "src/main/resources/scripts/",
        defaultValue = CoreConfigKey.SCAN_PATH.default
    )

    private val identifierRegex: String? by detailedOption(
        help = "regex to extract identifier part from scripts",
        example = "v(.*)_.*",
        defaultValue = CoreConfigKey.SCAN_IDENTIFIER_REGEX.default
    )

    private val whitelistedTags: String? by detailedOption(
        help = "tags to whitelist (separated by ',')",
        example = "WHITELISTED_TAG1,WHITELISTED_TAG2"
    )

    private val blacklistedTags: String? by detailedOption(
        help = "tags to blacklist (separated by ',')",
        example = "BLACKLISTED_TAG1,BLACKLISTED_TAG2"
    )

    private val tagsToPlayAgain: String? by detailedOption(
        help = "tags to play again at each datamaintain execution (separated by ',')",
        example = "TAG_TO_PLAY_AGAIN1,TAG_TO_PLAY_AGAIN2"
    )

    private val createTagsFromFolder: Boolean? by detailedOption(
        help = "create automatically tags from parent folders",
        defaultValue = CoreConfigKey.CREATE_TAGS_FROM_FOLDER.default
    ).flag()

    private val executionMode by detailedOption(
        help = "execution mode",
        defaultValue = CoreConfigKey.EXECUTION_MODE.default
    ).choice(ExecutionMode.values().map { it.name }.map { it to it }.toMap())

    private val action by detailedOption(
        help = "script action",
        defaultValue = CoreConfigKey.DEFAULT_SCRIPT_ACTION.default
    ).choice(ScriptAction.values().map { it.name }.map { it to it }.toMap())

    private val allowAutoOverride: Boolean? by detailedOption(
        help = "Allow datamaintain to automaticaly override scripts",
        defaultValue = CoreConfigKey.PRUNE_OVERRIDE_UPDATED_SCRIPTS.default
    ).flag()

    private val verbose: Boolean? by detailedOption(
        help = "verbose",
        defaultValue = CoreConfigKey.VERBOSE.default
    ).flag()

    private val saveDbOutput: Boolean? by detailedOption(
        help = "save your script and db output",
        defaultValue = DriverConfigKey.DB_SAVE_OUTPUT.default
    ).flag()

    private val printDbOutput: Boolean? by detailedOption(
        help = "print your script and db output",
        defaultValue = DriverConfigKey.DB_PRINT_OUTPUT.default
    ).flag()

    private val tagsMatchers: List<Pair<String, String>>? by detailedOption(
        "--tag",
        help = "Tag defined using glob path matchers. " +
            "To define multiple tags, use option multiple times. " +
            "Syntax example: MYTAG1=[pathMatcher1, pathMatcher2]",
        example = "MYTAG1=[pathMatcher1, pathMatcher2]",
        defaultValue = CoreConfigKey.TAG.default
    )
        .convert {
            val split = it.split("=")
            Pair(split[0], split[1])
        }
        .multiple()

    private val checkRules: List<String>? by detailedOption(
        "--rule",
        help = "check rule to play. " +
                "To define multiple rules, use option multiple times.",
        defaultValue = CoreConfigKey.CHECK_RULES.default
    )
        .choice(allCheckRuleNames.map { it to it }.toMap())
        .multiple()

    private val mongoShell: String? by detailedOption(
        help = "mongo binary, can be mongo or mongosh. mongo by default",
        defaultValue = MongoConfigKey.DB_MONGO_SHELL.default?.toLowerCase()
    ).choice(MongoShell.values().map { it.name }.map { it.toLowerCase() }.associateWith { it })

    private val porcelain: Boolean? by detailedOption(
        help = "for each executed script, display relative path to scan path",
        defaultValue = CoreConfigKey.PRINT_RELATIVE_PATH_OF_SCRIPT.default
    ).flag()

    private val flags: List<String>? by option(help = "add a flag on the executed scripts. " +
            "To define multiple rules, use option multiple times.")
        .multiple()

    private val datamaintainMonitoringApiUrl: String? by option(help = "Url to contact the datamaintain monitoring app." +
            "Will be used to send all reports on executions")

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
        datamaintainMonitoringApiUrl?.let { props.put(CoreConfigKey.DATAMAINTAIN_MONITORING_API_URL.key, it) }
    }

    fun List<String>.optionListToString(): String = this.joinToString(",")
}
