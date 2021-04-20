package datamaintain.cli.update.db

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import datamaintain.cli.utils.loadConfig
import datamaintain.core.Datamaintain
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.script.ScriptAction
import datamaintain.core.step.check.allCheckRuleNames
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.db.driver.mongo.MongoConfigKey
import java.util.*
import kotlin.system.exitProcess

private fun defaultUpdateDbRunner(config: DatamaintainConfig) {
    Datamaintain(config).updateDatabase().print(config.verbose)
}

class UpdateDb(val runner: (DatamaintainConfig) -> Unit = ::defaultUpdateDbRunner) : CliktCommand(name = "update-db") {

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

    private val mongoSaveOutput: Boolean? by option(help = "save mongo output").flag()

    private val mongoPrintOutput: Boolean? by option(help = "print mongo output").flag()

    private val props by requireObject<Properties>()

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

    override fun run() {
        var config: DatamaintainConfig? = null
        try {
            overloadPropsFromArgs(props)
            config = loadConfig(props)
            runner(config)
        } catch (e: DatamaintainException) {
            val verbose: Boolean = config?.verbose ?: false

            echo("Error at step ${e.step}", err = true)
            e.report.print(verbose)
            echo("")
            echo(e.message, err = true)

            if (e.resolutionMessage.isNotEmpty()) {
                echo(e.resolutionMessage)
            }

            exitProcess(1)
        } catch (e: DatamaintainBaseException) {
            echo(e.message, err = true)
            echo(e.resolutionMessage)

            exitProcess(1)
        } catch (e: IllegalArgumentException) {
            echo(e.message, err = true)
            exitProcess(1)
        } catch (e: Exception) {
            echo(e.message ?: "unexpected error", err = true)
            exitProcess(1)
        }
    }

    private fun overloadPropsFromArgs(props: Properties) {
        path?.let { props.put(CoreConfigKey.SCAN_PATH.key, it) }
        identifierRegex?.let { props.put(CoreConfigKey.SCAN_IDENTIFIER_REGEX.key, it) }
        whitelistedTags?.let { props.put(CoreConfigKey.TAGS_WHITELISTED.key, it) }
        blacklistedTags?.let { props.put(CoreConfigKey.TAGS_BLACKLISTED.key, it) }
        tagsToPlayAgain?.let { props.put(CoreConfigKey.PRUNE_TAGS_TO_RUN_AGAIN.key, it) }
        createTagsFromFolder?.let { props.put(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.key, it.toString()) }
        verbose?.let { props.put(CoreConfigKey.VERBOSE.key, it.toString()) }
        mongoSaveOutput?.let { props.put(MongoConfigKey.DB_MONGO_SAVE_OUTPUT.key, it.toString()) }
        mongoPrintOutput?.let { props.put(MongoConfigKey.DB_MONGO_PRINT_OUTPUT.key, it.toString()) }
        executionMode?.let { props.put(CoreConfigKey.EXECUTION_MODE.key, it) }
        action?.let { props.put(CoreConfigKey.DEFAULT_SCRIPT_ACTION.key, it) }
        tagsMatchers?.forEach {
            props.put("${CoreConfigKey.TAG.key}.${it.first}", it.second)
        }
        checkRules?.let { props.put(CoreConfigKey.CHECK_RULES.key, it.joinToString(",")) }
        allowAutoOverride?.let { props.put(CoreConfigKey.PRUNE_OVERRIDE_UPDATED_SCRIPTS.key, it.toString()) }
    }
}
