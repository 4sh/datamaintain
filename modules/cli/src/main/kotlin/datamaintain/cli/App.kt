package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import datamaintain.core.config.Config
import datamaintain.core.runDatamaintain
import datamaintain.db.driver.mongo.MongoDriverConfig
import java.io.File
import java.util.*
import kotlin.system.exitProcess

val dbValues = listOf("mongo")

class App : CliktCommand() {

    private val configFilePath: File? by option(help = "path to config file")
            .convert { File(it) }
            .validate {
                it.exists()
            }

    private val path: String? by option(help = "path to directory containing scripts")
            .default("./scripts")

    private val dbType: String by option(help = "db type : ${dbValues.joinToString(",")}")
            .default("mongo")
            .validate {
                dbValues.contains(it)
            }

    private val mongoDbName: String? by option(help = "mongo db name")

    override fun run() {
        try {
            val props = Properties()
            configFilePath?.let {
                props.load(it.inputStream())
            }

            val config = loadConfig(props)

            val driverConfig = loadDriverConfig(props)

            runDatamaintain(config, driverConfig)
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
        }.run {
            this.copy(dbName = mongoDbName ?: this.dbName)
        }
    }

    private fun loadConfig(props: Properties): Config {
        return Config.buildConfig(props)
                .run {
                    copy(path = path)
                }
    }
}

fun main(args: Array<String>) {
    App().main(args)
}
