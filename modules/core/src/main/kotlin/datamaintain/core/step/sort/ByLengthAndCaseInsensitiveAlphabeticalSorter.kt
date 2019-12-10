package datamaintain.core.step.sort

import datamaintain.core.Config
import datamaintain.core.script.Script

class ByLengthAndCaseInsensitiveAlphabeticalSorter(config: Config) : Sorter<String>(config) {
    override fun <T : Script> sort(scripts: List<T>, getter: (T) -> String): List<T> {
        return scripts.sortedWith(Comparator { script1, script2 ->
            val value1 = getter(script1)
            val value2 = getter(script2)

            var comparisonResult = value1.length.compareTo(value2.length)

            if (comparisonResult == 0) {
                comparisonResult = value1.toLowerCase().compareTo(value2.toLowerCase())
            }

            comparisonResult
        })
    }
}
