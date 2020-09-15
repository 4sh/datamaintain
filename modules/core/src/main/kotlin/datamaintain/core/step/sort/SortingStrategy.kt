package datamaintain.core.step.sort

import datamaintain.core.script.Script

abstract class SortingStrategy<U>() {
    abstract fun <T : Script> sort(scripts: List<T>, getter: (T) -> U): List<T>
}