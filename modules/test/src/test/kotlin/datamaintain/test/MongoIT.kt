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
                "--path", "src/test/resources/integration/ok",
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
        expectThat(collection.countDocuments()).isEqualTo(3)
    }

    @Test
    fun `should partial execute`() {
        // Given
        main(arrayOf(
                "--path", "src/test/resources/integration/partial",
                "--identifier-regex", "(.*?)_.*",
                "--db-type", "mongo",
                "--mongo-uri", mongoUri
        ))

        expectThat(database.getCollection("simple").countDocuments()).isEqualTo(2)
        expectThat(collection.countDocuments()).isEqualTo(2)

        val args = arrayOf(
                "--path", "src/test/resources/integration/ok",
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
        expectThat(collection.countDocuments()).isEqualTo(3)
    }

    @Test
    fun `should dry run`() {
        // Given
        val args = arrayOf(
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--db-type", "mongo",
                "--execution-mode", "DRY",
                "--mongo-uri", mongoUri
        )

        // When
        main(args)

        // Then
        val coll = database.getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(0)

        // TODO check report displayed ?

        // TODO use list executed option to check
        expectThat(collection.countDocuments()).isEqualTo(0)
    }

    @Test
    fun `should force mark as executed`() {
        // Given
        val args = arrayOf(
                "--path", "src/test/resources/integration/ok",
                "--identifier-regex", "(.*?)_.*",
                "--db-type", "mongo",
                "--execution-mode", "FORCE_MARK_AS_EXECUTED",
                "--mongo-uri", mongoUri
        )

        // When
        main(args)

        // Then
        val coll = database.getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(0)

        // TODO check report displayed ?

        // TODO use list executed option to check
        expectThat(collection.countDocuments()).isEqualTo(3)

    }

    @Test
    fun `should fail with invalid script`() {
        // Given
        val args = arrayOf(
                "--path", "src/test/resources/integration/ko",
                "--identifier-regex", "(.*?)_.*",
                "--db-type", "mongo",
                "--mongo-uri", mongoUri
        )

        // When
        main(args)

        // Then
        val coll = database.getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(1)

        // TODO check report displayed ?

        // TODO use list executed option to check
        expectThat(collection.countDocuments()).isEqualTo(1)
    }
}