package datamaintain.cli.app.utils

import java.nio.file.Path

fun extractRelativePath(scanPath: Path, filePath: Path): String =
    filePath.toString().removePrefix(scanPath.toString()).trim('/')
