package datamaintain.sort

import datamaintain.Config
import datamaintain.Script

class CaseInsensitiveAlphabeticalSorter(config: Config) : Sorter<String>(config) {
    override fun <T : Script> sort(scripts: List<T>, getter: (T) -> String): List<T> {
        return scripts.sortedBy { script: T -> getter(script).toLowerCase() }
    }
}