package datamaintain.db.driver.mongo

import datamaintain.db.driver.mongo.exception.DatamaintainMongoUriException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Nested
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.failed
import strikt.assertions.isA
import strikt.assertions.isEqualTo

internal class MongoConnectionStringBuilderTest {
    @Nested
    inner class StartOfURI {
        @Test
        @DisplayName("should throw error when uri starts neither with mongodb:// nor mongodb+srv://")
        fun `should throw error when uri starts neither with mongodb nor mongodb+srv` () {
            // Given
            val mongoUri = "host1/databasename"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        @DisplayName("should accept uri starting with mongodb://")
        fun `should accept uri starting with mongodb` () {
            // Given
            val mongoUri = "mongodb://host1/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }

        @Test
        @DisplayName("should accept uri starting with mongodb:+srv//")
        fun `should accept uri starting with mongodb+srv` () {
            // Given
            val mongoUri = "mongodb+srv://host1/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }

    }

    @Nested
    inner class Authentication {
        @Test
        fun `should throw error when username is given but not password` () {
            // Given
            val mongoUri = "mongodb://username@host1/databasename"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        @DisplayName("should accept mongo uri with username and password separated by :")
        fun `should accept mongo uri with username and password correctly separated` () {
            // Given
            val mongoUri = "mongodb://username:password@host1/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }
    }

    @Nested
    inner class Hosts {
        @Test
        fun `should throw error when no host is given` () {
            // Given
            val mongoUri = "mongodb:///databasename"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        fun `should accept mongo uri with one host but no port` () {
            // Given
            val mongoUri = "mongodb://host1/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }

        @Test
        fun `should accept mongo uri with host containing dots` () {
            // Given
            val mongoUri = "mongodb://www.host.com/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }

        @Test
        fun `should accept mongo uri with host containing dashes` () {
            // Given
            val mongoUri = "mongodb://my-host/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }


        @Test
        fun `should throw error when port is not a number` () {
            // Given
            val mongoUri = "mongodb://host1:port/databasename"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        @DisplayName("should throw error when port is not separated from host by :")
        fun `should throw error when port is wrongly separated from host` () {
            // Given
            val mongoUri = "mongodb://host1!8080/databasename"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        fun `should accept mongo uri with one host and its port` () {
            // Given
            val mongoUri = "mongodb://host1:8080/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }
    }

    @Nested
    inner class DatabaseName {
        @Test
        fun `should throw error when database name and slash are missing` () {
            // Given
            val mongoUri = "mongodb://host1:8080"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        fun `should throw error when database name is missing` () {
            // Given
            val mongoUri = "mongodb://host1:8080/"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        fun `should throw error when database name contains a collection name` () {
            // Given
            val mongoUri = "mongodb://host1:8080/databasename.collection"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        fun `should accept mongo uri when database name is present and does not contain a collection name` () {
            // Given
            val mongoUri = "mongodb://host1/databasename"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }
    }

    @Nested
    inner class Options {
        @Test
        @DisplayName("should throw error when option are not formatted like this: name=value")
        fun `should throw error when options are wrongly formatted` () {
            // Given
            val mongoUri = "mongodb://host1:8080/databasename?name:value"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        fun `should throw error when missing ? before options` () {
            // Given
            val mongoUri = "mongodb://host1:8080/databasenamename=value"

            // When

            // Then
            expectCatching { MongoConnectionStringBuilder().buildConnectionString(mongoUri, false) }
                    .failed()
                    .isA<DatamaintainMongoUriException>()
        }

        @Test
        fun `should accept mongo uri when options are correctly formatted` () {
            // Given
            val mongoUri = "mongodb://host1/databasename?name=value"

            // When
            val connectionString = MongoConnectionStringBuilder().buildConnectionString(mongoUri, false)

            // Then
            expectThat(connectionString).isEqualTo(mongoUri)
        }
    }
}
