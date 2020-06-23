package datamaintain.core.step.sort

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.Script

class ByCaseInsensitiveSeparatorFreeAlphabeticalSorter(config: DatamaintainConfig) : Sorter<String>(config) {
    override fun <T : Script> sort(scripts: List<T>, getter: (T) -> String): List<T> {
        return scripts.sortedWith(Comparator { script1, script2 ->
            val value1 = refineValue<T>(getter(script1))
            val value2 = refineValue<T>(getter(script2))

            value1.toLowerCase().compareTo(value2.toLowerCase())
        })
    }

    private fun <T : Script> refineValue(value: String) = value.replace(Regex("[^0-9a-z-A-Z]"), "0")
}
