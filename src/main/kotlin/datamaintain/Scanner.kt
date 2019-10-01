package datamaintain

class Scanner(private val config: Config) {
    fun scan(): List<ScriptWithContent> = config.path.toFile().walk()
                .filter { it.isFile }
                .map {
                        FileScript(it.toPath())
                }
                .sortedBy { it.name }.toList()
}