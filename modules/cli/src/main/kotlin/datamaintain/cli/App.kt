package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.runDatamaintain
import datamaintain.core.script.Tag
import datamaintain.db.driver.mongo.MongoDriverConfig
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

val dbValues = listOf("mongo")

class App : CliktCommand() {

    private val configFilePath: File? by option(help = "path to config file")
            .convert { File(it) }
            .validate { it.exists() }

    private val path: Path? by option(help = "path to directory containing scripts")
            .convert { Paths.get(it) }
            .default(Paths.get("./scripts"))
            .validate { it.toFile().exists() }

    private val dbType: String by option(help = "db type : ${dbValues.joinToString(",")}")
            .default("mongo")
            .validate { dbValues.contains(it) }

    private val identifierRegex: Regex? by option(help = "regex to extract identifier part from scripts")
            .convert { Regex(it) }

    private val blacklistedTags: Set<Tag>? by option(help = "tags to blacklist")
            .convert { it.split(",").map { tag -> Tag(tag) }.toSet() }

    private val mongoDbName: String? by option(help = "mongo db name")

    private val mongoUri: String? by option(help = "mongo uri")

    private val mongoTmpPath: Path? by option(help = "mongo tmp file path")
            .convert { Paths.get(it) }

    private val tags: Set<Tag>? by option(help = "list of your tags defined using glob path matchers, like this: " +
            "MYTAG1=[pathMatcher1; pathMatcher2],MYTAG2=[pathMatcher3]...")
            .convert {
                it.split(",")
                        .map { tagDeclaration -> tagDeclaration.split("=") }
                        .map { splitTagDeclaration ->
                            Tag(splitTagDeclaration[0],
                                    pathMatchers = splitTagDeclaration[1].split(";")
                                            .map { pathMatcher -> FileSystems.getDefault().getPathMatcher("glob:" + pathMatcher) }
                                            .toSet())
                        }.toSet()
            }

    override fun run() {
        try {
            val props = Properties()
            configFilePath?.let {
                props.load(it.inputStream())
            }

            val config = loadConfig(props)

            runDatamaintain(config)

        } catch (e: Exception) {
            echo(e.message ?: "unexpected error")
            exitProcess(0)
        }
    }

    private fun loadDriverConfig(props: Properties): MongoDriverConfig {
        return when (dbType) {
            "mongo" -> MongoDriverConfig.buildConfig(props)
            else -> {
                echo("dbType $dbType is unknown")
                exitProcess(0)
            }
        }.let {
            it.copy(
                    dbName = this.mongoDbName ?: it.dbName,
                    mongoUri = this.mongoUri ?: it.mongoUri,
                    tmpFilePath = this.mongoTmpPath ?: it.tmpFilePath
            )
        }
    }

    private fun loadConfig(props: Properties): DatamaintainConfig {
        val driverConfig = loadDriverConfig(props)
        return DatamaintainConfig.buildConfig(props, driverConfig)
                .let {
                    it.copy(
                            path = this.path ?: it.path,
                            identifierRegex = this.identifierRegex ?: it.identifierRegex,
                            blacklistedTags = this.blacklistedTags ?: it.blacklistedTags,
                            tags = this.tags?: it.tags
                    )
                }
    }
}

fun main(args: Array<String>) {
    App().main(args)
}
