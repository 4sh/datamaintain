package datamaintain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import datamaintain.cli.update.db.MarkOneScriptAsExecuted
import datamaintain.cli.update.db.UpdateDb
import datamaintain.core.db.driver.DriverConfigKey
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.db.driver.mongo.MongoConfigKey
import java.io.File
import java.util.*

class App : CliktCommand() {

    private val configFilePath: File? by option(help = "path to config file")
            .convert { File(it) }
            .validate { it.exists() }

    private val dbType: String? by option(help = "db type : ${DbType.values().joinToString(",") { v -> v.value }}")
            .validate { DbType.values().map { v -> v.value }.contains(it) }

    private val dbUri: String? by option(help = "mongo uri with at least database name. Ex: mongodb://localhost:27017/newName")

    private val trustUri: Boolean? by option(help = "Deactivate all controls on the URI you provide Datamaintain").flag()

    private val mongoTmpPath: String? by option(help = "mongo tmp file path")

    private val props by findObject() { Properties() }

    override fun run() {
        configFilePath?.let {
            props.load(it.inputStream())
        }
        overloadPropsFromArgs(props)
    }

    private fun overloadPropsFromArgs(props: Properties) {
        dbType?.let { props.put("db.type", it) }

        // db type was mongo by default. To be backward compatible, set mongo as default is no type configured anywhere.
        if (!props.containsKey("db.type")) {
            props.put("db.type", "mongo")
        }

        dbUri?.let { props.put(DriverConfigKey.DB_URI.key, it) }
        mongoTmpPath?.let { props.put(MongoConfigKey.DB_MONGO_TMP_PATH.key, it) }
        trustUri?.let { props.put(DriverConfigKey.DB_TRUST_URI.key, it.toString()) }
    }

}

enum class DbType(val value: String) {
    MONGO("mongo"),
    JDBC("jdbc")
}

fun main(args: Array<String>) {
    App().subcommands(UpdateDb(), ListExecutedScripts(), MarkOneScriptAsExecuted()).main(args)
}

class DbTypeNotFoundException(private val dbType: String) : DatamaintainBaseException("dbType $dbType is unknown")
