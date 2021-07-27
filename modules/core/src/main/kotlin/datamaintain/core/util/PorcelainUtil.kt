package datamaintain.core.util

import java.nio.file.Path

fun extractRelativePath(scanPath: Path, filePath: Path): String =
    filePath.toString().removePrefix(scanPath.toString())
