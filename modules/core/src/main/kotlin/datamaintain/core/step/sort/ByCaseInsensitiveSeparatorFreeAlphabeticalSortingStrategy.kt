package datamaintain.core.step.sort

import datamaintain.core.script.Script

/**
 * Allow to sort scripts considering the parts of the value returned by the getter.
 * The parts list is given by a split on all chars that are not a figure or a letter.
 * The number parts would be sorted as number, and the others as string (so with alphabetical sort)
 *
 * We assume the differents values have an homogeneity. For instance, you will have troubles
 * to sort ["abc.123", "abc.234", "abc.0001", "abc.def"]
 */
class ByCaseInsensitiveSeparatorFreeAlphabeticalSortingStrategy : SortingStrategy<String>() {
    private val numberRegex = "^\\d*$".toRegex()

    override fun <T : Script> sort(scripts: List<T>, getter: (T) -> String): List<T> {
        return scripts.sortedWith(Comparator { script1, script2 ->
            val value1 = split(getter(script1))
            val value2 = split(getter(script2))

            var compareResult = 0;

            for ((index, part1) in value1.withIndex()) {
                if (value2.size <= index) {
                    // value1 is after value2 because value1 has more parts than value2
                    compareResult = 1;
                    break
                } else {
                    val part2 = value2.get(index)

                    val lengthComparison = part1.length.compareTo(part2.length)

                    if (lengthComparison != 0) {
                        // The length are not equals.

                        // When part are number, then we can simply sort them assuming their length
                        if (numberRegex.matches(part1)) {
                            // The part having the smallest size is before
                            compareResult = lengthComparison
                            break
                        }
                    }

                    val compareTo = part1.compareTo(part2)

                    if (compareTo != 0) {
                        // The 2 part are not equals so we know the order of the scripts
                        compareResult = compareTo;
                        break
                    }
                }
            }

            if (compareResult == 0 && value1.size != value2.size) {
                // The last case is when value2 is longer than value1. It means value2 has more parts than value1,
                // so value2 is after value1
                compareResult = -1;
            }

            compareResult;
        })
    }

    private fun split(value: String) = value.toLowerCase().split(Regex("[^0-9a-zA-Z]"))
}
