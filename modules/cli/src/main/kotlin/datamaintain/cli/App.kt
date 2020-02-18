package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.runDatamaintain
import datamaintain.core.script.Tag
import datamaintain.db.driver.mongo.MongoConfigKey
import datamaintain.db.driver.mongo.MongoDriverConfig
import java.io.File
import java.util.*
import kotlin.system.exitProcess

class App : CliktCommand() {

    private val configFilePath: File? by option(help = "path to config file")
            .convert { File(it) }
            .validate { it.exists() }

    private val dbType: String by option(help = "db type : ${DbType.values().joinToString(",") { v -> v.value }}")
            .default("mongo")
            .validate { DbType.values().map { v -> v.value }.contains(it) }

    private val path: String? by option(help = "path to directory containing scripts")

    private val identifierRegex: String? by option(help = "regex to extract identifier part from scripts")

    private val blacklistedTags: String? by option(help = "tags to blacklist (separated by ','")

    private val createTagsFromFolder: String? by option(help = "boolean to create automatically tags from parent folders (true or false)")

    private val executionMode: String? by option(help = "execution mode (NORMAL or DRY)")

    private val mongoDbName: String? by option(help = "mongo db name")

    private val mongoUri: String? by option(help = "mongo uri")

    private val mongoTmpPath: String? by option(help = "mongo tmp file path")

    private val tags: List<Tag>? by option("--tag", help = "Tag defined using glob path matchers. " +
            "To define multiple tags, use option multiple times. " +
            "Syntax example: MYTAG1=[pathMatcher1; pathMatcher2]")
            .convert {
                val split = it.split("=")
                Tag.parse(split[0], split[1])
            }
            .multiple()

    override fun run() {
        try {
            val props = Properties()
            configFilePath?.let {
                props.load(it.inputStream())
            }

            overloadPropsFromArgs(props)

            val config = loadConfig(props)

            runDatamaintain(config)

        } catch (e: Exception) {
            echo(e.message ?: "unexpected error")
            exitProcess(0)
        }
    }

    private fun overloadPropsFromArgs(props: Properties) {
        path?.let { props.put(CoreConfigKey.SCAN_PATH.key, it) }
        identifierRegex?.let { props.put(CoreConfigKey.SCAN_IDENTIFIER_REGEX.key, it) }
        blacklistedTags?.let { props.put(CoreConfigKey.TAGS_BLACKLISTED.key, it) }
        createTagsFromFolder?.let { props.put(CoreConfigKey.CREATE_TAGS_FROM_FOLDER.key, it) }
        executionMode?.let { props.put(CoreConfigKey.EXECUTION_MODE.key, it) }
        tags?.let { props.put(CoreConfigKey.TAGS, it.toSet()) }
        mongoDbName?.let { props.put(MongoConfigKey.DB_MONGO_DBNAME.key, it) }
        mongoUri?.let { props.put(MongoConfigKey.DB_MONGO_URI.key, it) }
        mongoTmpPath?.let { props.put(MongoConfigKey.DB_MONGO_TMP_PATH.key, it) }
    }

    private fun loadDriverConfig(props: Properties): MongoDriverConfig {
        return when (dbType) {
            DbType.MONGO.value -> MongoDriverConfig.buildConfig(props)
            else -> {
                echo("dbType $dbType is unknown")
                exitProcess(0)
            }
        }
    }

    private fun loadConfig(props: Properties): DatamaintainConfig {
        val driverConfig = loadDriverConfig(props)
        return DatamaintainConfig.buildConfig(driverConfig, props)
    }

    enum class DbType(val value: String) {
        MONGO("mongo")
    }
}

fun main(args: Array<String>) {
    App().main(args)
}
