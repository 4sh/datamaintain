package datamaintain.core.step.sort

import datamaintain.core.script.Script

class ByCaseInsensitiveSeparatorFreeAlphabeticalSortingStrategy : SortingStrategy<String>() {
    override fun <T : Script> sort(scripts: List<T>, getter: (T) -> String): List<T> {
        return scripts.sortedWith(Comparator { script1, script2 ->
            val value1 = split(getter(script1))
            val value2 = split(getter(script2))

            var compareResult = 0;

            for ((index, part1) in value1.withIndex()) {
                if (value2.size <= index) {
                    // value1 is after value2
                    compareResult = 1;
                    break
                } else {
                    val part2 = value2.get(index)

                    val lengthComparison = part1.length.compareTo(part2.length)

                    if (lengthComparison != 0) {
                        compareResult = lengthComparison
                        break
                    }

                    val compareTo = part1.compareTo(part2)

                    if (compareTo != 0) {
                        compareResult = compareTo;
                        break
                    }
                }
            }

            if (compareResult == 0 && value1.size != value2.size) {
                // value2 is after value1
                compareResult = -1;
            }

            compareResult;
        })
    }

    private fun split(value: String) = value.toLowerCase().split(Regex("[^0-9a-zA-Z]"))
}
