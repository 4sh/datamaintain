package datamaintain.sort

import datamaintain.Config
import datamaintain.Script

abstract class Sorter<U>(protected val config: Config) {
    abstract fun <T : Script> sort(scripts: List<T>, getter: (T) -> U): List<T>
}