package datamaintain

import java.nio.file.Path

data class Config(val path: Path,
                  val mongoUri: String)