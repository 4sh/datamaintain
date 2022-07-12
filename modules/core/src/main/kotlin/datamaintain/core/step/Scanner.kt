package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.script.FileScript
import datamaintain.domain.script.ScriptWithContent
import datamaintain.domain.script.Tag
import mu.KotlinLogging
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

class Scanner(private val context: Context) {
    private val scannerConfig
        get() = context.config.scanner
    
    fun scan(): List<ScriptWithContent> {
        try {
            val rootFolder: File = scannerConfig.path.toFile()
            if (!context.config.logs.porcelain) { logger.info { "Scan ${rootFolder.absolutePath}..." } }
            val scannedFiles = rootFolder.walk()
                    .filter { it.isFile }
                    .map { FileScript.from(context.config, buildTags(rootFolder, it).toSet(), it) }
                    .sortedBy { it.name }
                    .onEach { context.reportBuilder.addScannedScript(it) }
                    .onEach {
                        if (context.config.logs.verbose && !context.config.logs.porcelain) {
                            logger.info { "${it.name} is scanned" }
                        }
                    }
                    .toList()
            if (!context.config.logs.porcelain) { logger.info { "${scannedFiles.size} files scanned" } }
            scannerConfig.tagsMatchers.onEach { tagMatcher ->
                if (scannedFiles.none { it.tags.contains(tagMatcher.tag) }) {
                    if (!context.config.logs.porcelain) { logger.warn {"WARNING: ${tagMatcher.tag} did not match any scripts"} }
                }
            }
            if (!context.config.logs.porcelain) {
                logger.info { "" }
                if (scannedFiles.isEmpty()) { logger.warn { "WARNING: No scripts were found" } }
            }
            return scannedFiles
        } catch (datamaintainException: DatamaintainBaseException) {
            throw DatamaintainException(
                datamaintainException.message,
                Step.SCAN,
                context.reportBuilder,
                datamaintainException.resolutionMessage
            )
        }
    }

    private fun buildTags(rootFolder: File, file: File): Set<Tag> {
        val tags = mutableSetOf<Tag>()

        if (scannerConfig.doesCreateTagsFromFolder) {
            tags.addAll(buildTagsFromFolder(rootFolder, file))
        }

        tags.addAll(scannerConfig.tagsMatchers.filter { it.matches(file.toPath()) }.map { it.tag })

        return tags
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
