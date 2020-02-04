package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import datamaintain.core.Datamaintain
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.db.driver.mongo.MongoConfigKey
import datamaintain.db.driver.mongo.MongoDriverConfig
import mu.KotlinLogging
import java.io.File
import java.util.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class App : CliktCommand() {

    private val configFilePath: File? by option(help = "path to config file")
            .convert { File(it) }
            .validate { it.exists() }

    private val dbType: String by option(help = "db type : ${DbType.values().joinToString(",") { v -> v.value }}")
            .default("mongo")
            .validate { DbType.values().map { v -> v.value }.contains(it) }

    private val mongoUri: String? by option(help = "mongo uri with at least database name")

    private val mongoTmpPath: String? by option(help = "mongo tmp file path")

    private val verbose: String? by option(help = "verbose")

    private val props by findObject() { Properties() }

    override fun run() {
        configFilePath?.let {
            props.load(it.inputStream())
        }
        overloadPropsFromArgs(props)
        props.put("dbType", dbType)
    }

    private fun overloadPropsFromArgs(props: Properties) {
        verbose?.let { props.put(CoreConfigKey.VERBOSE.key, it) }
        mongoUri?.let { props.put(MongoConfigKey.DB_MONGO_URI.key, it) }
        mongoTmpPath?.let { props.put(MongoConfigKey.DB_MONGO_TMP_PATH.key, it) }
    }

}

class UpdateDb : CliktCommand(name = "update-db") {

    private val path: String? by option(help = "path to directory containing scripts")

    private val identifierRegex: String? by option(help = "regex to extract identifier part from scripts")

    private val blacklistedTags: String? by option(help = "tags to blacklist (separated by ','")

    private val createTagsFromFolder: String? by option(help = "boolean to create automatically tags from parent folders (true or false)")

    private val executionMode: String? by option(help = "execution mode (NORMAL or DRY or FORCE_MARK_AS_EXECUTED)")

    private val props by requireObject<Properties>()

    override fun run() {
        try {
            overloadPropsFromArgs(props)
            val config = loadConfig(props)
            Datamaintain(config).updateDatabase().print(config.verbose)
        } catch (e: DbTypeNotFoundException) {
            echo("dbType ${e.dbType} is unknown")
            exitProcess(1)
        } catch (e: IllegalArgumentException) {
            echo(e.message)
            exitProcess(1)
        } catch (e: Exception) {
            echo(e.message ?: "unexpected error")
            exitProcess(1)
        }
    }

    private fun overloadPropsFromArgs(props: Properties) {
        path?.let { props.put(CoreConfigKey.SCAN_PATH.key, it) }
        identifierRegex?.let { props.put(CoreConfigKey.SCAN_IDENTIFIER_REGEX.key, it) }
        blacklistedTags?.let { props.put(CoreConfigKey.TAGS_BLACKLISTED.key, it) }
        createTagsFromFolder?.let { props.put(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.key, it) }
        executionMode?.let { props.put(CoreConfigKey.EXECUTION_MODE.key, it) }
    }
}

class List : CliktCommand() {

    private val props by requireObject<Properties>()

    override fun run() {
        try {
            val config = loadConfig(props)
            Datamaintain(config).listExecutedScripts().forEach {
                logger.info { "${it.name} (${it.checksum})" }
            }
        } catch (e: DbTypeNotFoundException) {
            echo("dbType ${e.dbType} is unknown")
            exitProcess(1)
        } catch (e: IllegalArgumentException) {
            echo(e.message)
            exitProcess(1)
        } catch (e: Exception) {
            echo(e.message ?: "unexpected error")
            exitProcess(1)
        }

    }
}

private fun loadConfig(props: Properties): DatamaintainConfig {
    val driverConfig = loadDriverConfig(props)
    return DatamaintainConfig.buildConfig(driverConfig, props)
}

private fun loadDriverConfig(props: Properties): MongoDriverConfig {
    return when (props.getProperty("dbType")) {
        DbType.MONGO.value -> MongoDriverConfig.buildConfig(props)
        else -> throw DbTypeNotFoundException(props.getProperty("dbType"))
    }
}

private enum class DbType(val value: String) {
    MONGO("mongo")
}

fun main(args: Array<String>) {
    App().subcommands(UpdateDb(), List()).main(args)
}

class DbTypeNotFoundException(val dbType: String) : RuntimeException()
