package datamaintain.core.step.sort

import datamaintain.core.script.Script
import java.util.*

/**
 * Allow to sort scripts considering the parts of the value returned by the getter.
 * The parts list is given by a split on all chars that are not a figure or a letter.
 * The number parts would be sorted as number, and the others as string (so with alphabetical sort)
 *
 * We assume the different values have a homogeneity. For instance, you will have troubles
 * to sort ["abc.123", "abc.234", "abc.0001", "abc.def"]
 */
class ByCaseInsensitiveSeparatorFreeAlphabeticalSortingStrategy : SortingStrategy<String>() {
    private val numberRegex = "^\\d*$".toRegex()

    override fun <T : Script> sort(scripts: List<T>, getter: (T) -> String): List<T> {
        return scripts.sortedWith { script1, script2 ->
            val value1 = split(getter(script1))
            val value2 = split(getter(script2))

            val compareResult: Int

            //Get index of the first difference or else null
            val minLength = if (value1.size < value2.size) value1.size else value2.size
            val lastIndex: Int? = (0 until minLength).dropWhile { value1[it] == value2[it] }.firstOrNull()

            compareResult =
                lastIndex
                    ?.let { index ->
                        //We stopped due to a difference
                        val part1 = value1[index]
                        val part2 = value2[index]

                        val lengthComparison = part1.length.compareTo(part2.length)

                        //String number with different length can't be compared with compareTo
                        //ex: "2".compareTo("12") = 1
                        if (lengthComparison != 0 && numberRegex.matches(part1)) {
                            lengthComparison
                        } else {
                            part1.compareTo(part2)
                        }
                    }
                    //We stopped due to reaching the end of one of the values
                    ?: value1.size.compareTo(value2.size)

            compareResult
        }
    }

    private fun split(value: String) = value.lowercase(Locale.getDefault()).split(Regex("[^\\da-zA-Z]"))
}
