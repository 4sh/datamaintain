package datamaintain.core.step

import datamaintain.core.Context
import datamaintain.core.script.FileScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.script.Tag
import java.io.File
import java.nio.file.Path

class Scanner(private val context: Context) {
    fun scan(): List<ScriptWithContent> {
        val rootFolder: File = context.config.path.toFile()

        return rootFolder.walk()
                .filter { it.isFile }
                .map {
                    val tags = mutableSetOf<Tag>()

                    if (context.config.doesCreateTagsFromFolder) {
                        tags.addAll(buildTagsFromFolder(rootFolder, it));
                    }

                    FileScript(it.toPath(), context.config.identifierRegex, tags.toSet())
                }
                .sortedBy { it.name }.toList()
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