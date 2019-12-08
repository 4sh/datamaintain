package datamaintain.core.step.sort

import datamaintain.core.Config
import datamaintain.core.script.Script

abstract class Sorter<U>(protected val config: Config) {
    abstract fun <T : Script> sort(scripts: List<T>, getter: (T) -> U): List<T>
}