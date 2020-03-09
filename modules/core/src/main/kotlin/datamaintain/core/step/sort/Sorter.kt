package datamaintain.core.step.sort

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.Script

abstract class Sorter<U>(protected val config: DatamaintainConfig) {
    abstract fun <T : Script> sort(scripts: List<T>, getter: (T) -> U): List<T>
}