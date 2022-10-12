package datamaintain.cli.completion

import datamaintain.core.util.execAppInSubprocess
import java.io.File

fun main() {
    val outputDir = "../../docs/auto-completion"
    System.out.println("Rebuilding auto-completion scripts in " + outputDir)

    listOf("bash", "zsh").forEach {
        val result = execAppInSubprocess(listOf("--generate-completion", it))
        File(outputDir + "/" + it + "-autocomplete.sh").writeText(result.output)
    }
}
