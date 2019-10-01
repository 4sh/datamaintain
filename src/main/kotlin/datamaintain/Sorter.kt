package datamaintain

class Sorter(val config: Config) {
    fun <T : Script> sort(scripts: List<T>): List<T> {
        return scripts.sortedBy {script -> script.name}
    }
}