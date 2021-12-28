package datamaintain.cli.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.choice
import datamaintain.cli.app.update.db.MarkOneScriptAsExecuted
import datamaintain.cli.app.update.db.UpdateDb
import datamaintain.cli.utils.CliSpecificKey
import datamaintain.core.config.CoreConfigKey
import datamaintain.core.db.driver.DriverConfigKey
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.db.driver.mongo.MongoConfigKey
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class App : CliktCommand() {
    private val workingDirectoryPath: Path? by option("--working-directory-path", "--wd", help = "path to the working directory. Can be relative but prefer absolute path. All relative paths configured will be relative to this path if set.")
            .convert { Paths.get(it) }
            .validate { it.toFile().exists() }

    private val configFilePath: File? by option(help = "path to config file")
        .convert { File(it) }
        .validate { it.exists() }

    private val dbType: String? by option(help = "db type")
        .choice(DbType.values().associate { v -> v.value to v.value })

    private val dbUri: String? by option(help = "mongo uri with at least database name. Ex: mongodb://localhost:27017/newName")

    private val trustUri: Boolean? by option(help = "Deactivate all controls on the URI you provide Datamaintain").flag()

    private val mongoTmpPath: String? by option(help = "mongo tmp file path")

    private val config: Boolean? by option(help = "Print the configuration without executing the subcommand").flag()

    private val props by findObject { Properties() }

    override fun run() {
        configFilePath?.let {
            val file = workingDirectoryPath?.resolve(it.toPath())?.toFile() ?: it

            props.load(file.inputStream())
        }

        overloadPropsFromArgs(props)
    }

    private fun overloadPropsFromArgs(props: Properties) {
        if (workingDirectoryPath != null) {
            props.put(CoreConfigKey.WORKING_DIRECTORY_PATH.key, workingDirectoryPath)
        } else {
            // The default working path is the parent of the config file
            configFilePath?.also {
                props.put(CoreConfigKey.WORKING_DIRECTORY_PATH.key, it.toPath().parent.toAbsolutePath().normalize().toString())
            }
        }


        dbUri?.let { props.put(DriverConfigKey.DB_URI.key, it) }
        dbType?.let { props[CoreConfigKey.DB_TYPE.key] = it }

        mongoTmpPath?.let { props.put(MongoConfigKey.DB_MONGO_TMP_PATH.key, it) }
        trustUri?.let { props.put(DriverConfigKey.DB_TRUST_URI.key, it.toString()) }

        config?.let { props.put(CliSpecificKey.__PRINT_CONFIG_ONLY.name, it.toString()) }
    }

}

enum class DbType(val value: String) {
    MONGO("mongo"),
    JDBC("jdbc")
}

val datamaintainApp = App().subcommands(UpdateDb(), ListExecutedScripts(), MarkOneScriptAsExecuted())

fun main(args: Array<String>) {
    datamaintainApp.main(args)
}

class DbTypeNotFoundException(private val dbType: String) : DatamaintainBaseException("db.type $dbType is unknown")
