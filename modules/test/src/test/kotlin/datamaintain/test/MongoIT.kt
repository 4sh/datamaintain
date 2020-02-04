package datamaintain.test

import datamaintain.cli.main
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class MongoIT : AbstractMongoDbTest() {

    @Test
    fun `should execute`() {
        // Given
        val args = arrayOf(
                "--path", "src/test/resources/integration",
                "--identifier-regex", "(.*?)_.*",
                "--db-type", "mongo",
                "--mongo-uri", mongoUri
        )

        // When
        main(args)

        // Then
        val coll = database.getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(3)

        // TODO check report displayed ?

        // TODO use list executed option to check
    }

    @Test
    fun `should force mark as executed`() {
        // Given
        val args = arrayOf(
                "--path", "src/test/resources/integration",
                "--identifier-regex", "(.*?)_.*",
                "--db-type", "mongo",
                "--execution-mode", "FORCE_MARK_AS_EXECUTED",
                "--db-type", "mongo",
                "--mongo-uri", mongoUri
        )

        // When
        main(args)

        // Then
        val coll = database.getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(0)

        // TODO check report displayed ?

        // TODO use list executed option to check
    }
}