package datamaintain.core.step.sort

import datamaintain.domain.script.Script

abstract class SortingStrategy<U>() {
    abstract fun <T : Script> sort(scripts: List<T>, getter: (T) -> U): List<T>
}