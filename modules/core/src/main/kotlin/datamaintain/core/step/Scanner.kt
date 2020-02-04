package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.Tag
import mu.KotlinLogging
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

class Scanner(private val context: Context) {
    fun scan(): List<ScriptWithContent> {
        val rootFolder: File = context.config.path.toFile()
        logger.info { "Scan ${rootFolder.absolutePath}..." }
        val scannedFiles = rootFolder.walk()
                .filter { it.isFile }
                .map {
                    val tags = mutableSetOf<Tag>()

                    if (context.config.doesCreateTagsFromFolder) {
                        tags.addAll(buildTagsFromFolder(rootFolder, it))
                    }

                    FileScript(it.toPath(), context.config.identifierRegex, tags.toSet())
                }
                .sortedBy { it.name }
                .onEach { context.reportBuilder.addScannedScript(it) }
                .onEach { }
                .toList()
        logger.info { "${scannedFiles.size} files scanned" }
        logger.info { "" }
        return scannedFiles
    }

    private fun buildTagsFromFolder(rootFolder: File, it: File): Set<Tag> {
        val parentTags = mutableSetOf<Tag>()
        val relativePath = it.relativeTo(rootFolder).toPath()

        var parent: Path? = relativePath.parent

        while (parent != null) {
            parentTags.add(Tag(parent.toFile().name))
            parent = parent.parent
        }

        return parentTags
    }
}
