package datamaintain.sort

import datamaintain.Config
import datamaintain.Script

class IdentifierSorter(config: Config) : Sorter(config) {
    override fun <T : Script> sort(scripts: List<T>): List<T> {
        return scripts.sortedBy { script -> script.identifier }
    }
}