package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.exception.DatamaintainBaseException
import datamaintain.core.exception.DatamaintainException
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.Tag
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
            logger.info { "Scan ${rootFolder.absolutePath}..." }
            val scannedFiles = rootFolder.walk()
                    .filter { it.isFile }
                    .sortedBy { it.name }
                    .map { FileScript.from(context.config, buildTags(rootFolder, it).toSet(), it) }
                    .onEach { context.reportBuilder.addScannedScript(it) }
                    .onEach { logger.debug { "${it.name} is scanned" } }
                    .toList()
            logger.info { "${scannedFiles.size} files scanned" }
            logger.trace { scannedFiles.map { it.name }}
            scannerConfig.tagsMatchers.onEach { tagMatcher ->
                if (scannedFiles.none { it.tags.contains(tagMatcher.tag) }) {
                    logger.warn {"WARNING: ${tagMatcher.tag} did not match any scripts"}
                }
            }
            logger.info { "" }
            if (scannedFiles.isEmpty()) { logger.warn { "WARNING: No scripts were found" } }
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
        logger.debug { "Search tags for ${file.path}" }
        val tags = mutableSetOf<Tag>()

        if (scannerConfig.doesCreateTagsFromFolder) {
            tags.addAll(buildTagsFromFolder(rootFolder, file))
        }

        tags.addAll(
            scannerConfig.tagsMatchers
                .filter {
                    val matches = it.matches(file.toPath())

                    if (matches) {
                        logger.debug { "${file.path} match tag ${it.tag} with glob ${it.globPaths}" }
                    } else {
                        logger.debug { "${file.path} does not match tag ${it.tag} with glob ${it.globPaths}" }
                    }

                    matches
                }
                .map { it.tag }
        )

        logger.debug { "${file.path} - Tags $tags" }
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
