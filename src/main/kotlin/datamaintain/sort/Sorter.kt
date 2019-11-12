package datamaintain.sort

import datamaintain.Config
import datamaintain.Script

abstract class Sorter(protected val config: Config) {
    abstract fun <T : Script> sort(scripts: List<T>): List<T>
}