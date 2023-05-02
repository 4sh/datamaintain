package datamaintain.test

import datamaintain.cli.app.main
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Paths

class MongoIT : AbstractMongoDbTest() {
    private val mongoCliDockerPath = javaClass.classLoader.getResource("mongo-cli/mongo_cli.sh")
        ?.file
        ?.let { Paths.get(it) }
        ?: error("Cannot find resources mongo-cli")

    @Test
    fun `should execute`() {
        // Given

        // When
        executeUpdateDbWithMongoClientInDocker(
            arrayOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri(),
            ),
            arrayOf(
                "--verbose",
                "update-db",
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--print-db-output",
                "--save-db-output"
            )
        )

        // Then
        val coll = database().getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(3)

        expectThat(listExecutedFiles())
                .hasSize(3)
                .containsExactly(
                        "01_file.js",
                        "02_file.js",
                        "03_file.js")
    }

    @Test
    fun `should partial execute`() {
        // Given
        executeUpdateDbWithMongoClientInDocker(
            arrayOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri(),
            ),
            arrayOf(
                "--verbose",
                "update-db",
                "--path", "src/test/resources/integration/partial",
                "--identifier-regex", "(.*?)_.*"
            )
        )

        expectThat(database().getCollection("simple").countDocuments()).isEqualTo(2)
        expectThat(collection().countDocuments()).isEqualTo(2)

        // When
        executeUpdateDbWithMongoClientInDocker(
            arrayOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri(),
            ),
            arrayOf(
                "--verbose",
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*"
            )
        )

        // Then
        expectThat(database().getCollection("simple").countDocuments()).isEqualTo(3)

        expectThat(listExecutedFiles())
                .hasSize(3)
                .containsExactly(
                        "01_file.js",
                        "02_file.js",
                        "03_file.js")
    }

    @Test
    fun `should dry run`() {
        // Given

        // When
        executeUpdateDbWithMongoClientInDocker(
            arrayOf("--db-type", "mongo"),
            arrayOf(
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--execution-mode", "DRY"
            )
        )

        // Then
        expectThat(database().getCollection("simple").countDocuments()).isEqualTo(0)

        expectThat(listExecutedFiles()).isEmpty()
    }

    @Test
    fun `should force mark as executed`() {
        // Given

        // When
        executeUpdateDbWithMongoClientInDocker(
            arrayOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri(),
            ),
            arrayOf(
                "--verbose",
                "update-db",
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--execution-mode", "NORMAL",
                "--action", "MARK_AS_EXECUTED"
            )
        )

        // Then
        expectThat(database().getCollection("simple").countDocuments()).isEqualTo(0)

        expectThat(listExecutedFiles())
                .hasSize(3)
                .containsExactly(
                        "01_file.js",
                        "02_file.js",
                        "03_file.js")

    }

    @Test
    fun `should override`() {
        // Given
        executeUpdateDbWithMongoClientInDocker(
            arrayOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri(),
            ),
            arrayOf(
                "--verbose",
                "update-db",
                "--path", "src/test/resources/integration/partial",
                "--identifier-regex", "(.*?)_.*"
            )
        )

        expectThat(database().getCollection("simple").countDocuments()).isEqualTo(2)
        expectThat(collection().countDocuments()).isEqualTo(2)

        // When
        executeUpdateDbWithMongoClientInDocker(
            arrayOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri()
            ),
            arrayOf(
                "--verbose",
                "--path", "src/test/resources/integration/override",
                "--identifier-regex", "(.*?)_.*",
                "--execution-mode", "NORMAL",
                "--allow-auto-override"
            )
        )

        // Then
        expectThat(listExecutedFiles())
                .hasSize(2)
                .containsExactly(
                        "01_file.js",
                        "02_file.js")
    }

    @Test
    fun `should fail with invalid script`() {
        // Given
        val mongoUri = this.mongoUri().replace("localhost", "host.docker.internal")

        val args = listOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri,
                "--verbose",
                "update-db",
                "--mongo-client", mongoCliDockerPath.toString(),
                "--verbose",
                "--path", "src/test/resources/integration/ko",
                "--identifier-regex", "(.*?)_.*"
        )

        // When
        val (exitCode, output) = execAppInSubprocess(args)

        // Then
        expectThat(exitCode).isEqualTo(1)
        expectThat(output)
            .and {
                contains("01_file.js executed")
                not { contains("02_file.js executed") }
                not { contains("03_file.js executed") }

                contains("02_file.js has not been correctly executed")
            }

        expectThat(database().getCollection("simple").countDocuments()).isEqualTo(1)

        expectThat(listExecutedFiles())
                .hasSize(1)
                .containsExactly("01_file.js")
    }

    private fun executeUpdateDbWithMongoClientInDocker(datamaintainArgs: Array<String>, updateArgs: Array<String>) {
        // mongo in test container must be call with the host host.docker.internal (over localhost)
        val mongoUri = this.mongoUri().replace("localhost", "host.docker.internal")
        val args = datamaintainArgs +
                arrayOf("--db-uri", mongoUri, "update-db", "--mongo-client", mongoCliDockerPath.toString()) +
                updateArgs

        main(args)
    }

    private fun listExecutedFiles(): List<String> {
        val out = System.out
        val baos = ByteArrayOutputStream()
        val ps = PrintStream(baos)
        System.setOut(ps)

        val mongoUri = this.mongoUri().replace("localhost", "host.docker.internal")
        main(arrayOf(
                "--db-type", "mongo",
                "--db-uri", mongoUri,
                "list",
                "--mongo-client", mongoCliDockerPath.toString()
        ))

        System.setOut(out)

        return String(baos.toByteArray())
                .split("\n")
                .map { it.split(" ").first() }
                .dropLast(1)
    }
}
