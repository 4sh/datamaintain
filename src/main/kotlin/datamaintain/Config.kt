package datamaintain

import datamaintain.db.drivers.DatamaintainDriver
import datamaintain.db.drivers.FakeDatamaintainDriver
import java.nio.file.Path

data class Config(val path: Path,
                  val mongoUri: String,
                  val identifierRegex: Regex,
                  val dbDriver: DatamaintainDriver = FakeDatamaintainDriver())