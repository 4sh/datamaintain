package datamaintain.test

import datamaintain.cli.main
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MongoIT : AbstractMongoDbTest() {

    @Test
    fun `should execute`() {
        // Given
        val args = arrayOf(
                "--db-type", "mongo",
                "--mongo-uri", mongoUri,
                "update-db",
                "--verbose", "true",
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--mongo-print-output", "true",
                "--mongo-save-output", "true"
        )

        // When
        main(args)

        // Then
        val coll = database.getCollection("simple")
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
        main(arrayOf(
                "--db-type", "mongo",
                "--mongo-uri", mongoUri,
                "--verbose", "true",
                "update-db",
                "--path", "src/test/resources/integration/partial",
                "--identifier-regex", "(.*?)_.*"

        ))

        expectThat(database.getCollection("simple").countDocuments()).isEqualTo(2)
        expectThat(collection.countDocuments()).isEqualTo(2)

        val args = arrayOf(
                "--db-type", "mongo",
                "--mongo-uri", mongoUri,
                "--verbose", "true",
                "update-db",
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*"
        )

        // When
        main(args)

        // Then
        expectThat(database.getCollection("simple").countDocuments()).isEqualTo(3)

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
        val args = arrayOf(
                "--db-type", "mongo",
                "--mongo-uri", mongoUri,
                "update-db",
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--execution-mode", "DRY"
        )

        // When
        main(args)

        // Then
        expectThat(database.getCollection("simple").countDocuments()).isEqualTo(0)

        expectThat(listExecutedFiles()).isEmpty()
    }

    @Test
    fun `should force mark as executed`() {
        // Given
        val args = arrayOf(
                "--db-type", "mongo",
                "--mongo-uri", mongoUri,
                "--verbose", "true",
                "update-db",
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--execution-mode", "FORCE_MARK_AS_EXECUTED"
        )

        // When
        main(args)

        // Then
        expectThat(database.getCollection("simple").countDocuments()).isEqualTo(0)

        expectThat(listExecutedFiles())
                .hasSize(3)
                .containsExactly(
                        "01_file.js",
                        "02_file.js",
                        "03_file.js")

    }

    @Test
    fun `should fail with invalid script`() {
        // Given
        val args = arrayOf(
                "--db-type", "mongo",
                "--mongo-uri", mongoUri,
                "--verbose", "true",
                "update-db",
                "--path", "src/test/resources/integration/ko",
                "--identifier-regex", "(.*?)_.*"
        )

        // When
        main(args)

        // Then
        expectThat(database.getCollection("simple").countDocuments()).isEqualTo(1)

        expectThat(listExecutedFiles())
                .hasSize(1)
                .containsExactly(
                        "01_file.js")
    }

    private fun listExecutedFiles(): List<String> {
        val out = System.out
        val baos = ByteArrayOutputStream()
        val ps = PrintStream(baos)
        System.setOut(ps)

        main(arrayOf(
                "--db-type", "mongo",
                "--mongo-uri", mongoUri,
                "list"
        ))

        System.setOut(out)

        return String(baos.toByteArray())
                .split("\n")
                .map { it.split(" ").first() }
                .dropLast(1)
    }
}
