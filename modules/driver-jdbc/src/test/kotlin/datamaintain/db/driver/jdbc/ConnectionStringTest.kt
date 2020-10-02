package datamaintain.db.driver.jdbc

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Nested
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.failed
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.lang.IllegalArgumentException

internal class ConnectionStringTest {
    @Nested
    inner class StartOfURI {
        @Test
        @DisplayName("should throw error when uri does not start with jdbc:sql-dbms:")
        fun `should throw error when uri does not start with jdbc dbms` () {
            // Given
            val jdbcUri = "host1/databasename"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        @DisplayName("should accept uri starting with jdbc:sql-dbms")
        fun `should accept uri starting with jdbc dbms` () {
            // Given
            val jdbcUri = "jdbc:postgres://host1/databasename"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }
    }

    @Nested
    inner class Authentication {
        @Test
        fun `should throw error when username is given but not password` () {
            // Given
            val jdbcUri = "jdbc:postgres//username@host1/databasename"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        @DisplayName("should accept dbms uri with username and password separated by :")
        fun `should accept dbms uri with username and password correctly separated` () {
            // Given
            val jdbcUri = "jdbc:postgres://username:password@host1/databasename"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }
    }

    @Nested
    inner class Hosts {
        @Test
        fun `should throw error when no host is given` () {
            // Given
            val jdbcUri = "jdbc:postgres:///databasename"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        fun `should accept jdbc uri with one host but no port` () {
            // Given
            val jdbcUri = "jdbc:postgres://host1/databasename"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }

        @Test
        fun `should accept jdbc uri with host containing dots` () {
            // Given
            val jdbcUri = "jdbc:postgres://www.host.com/databasename"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }

        @Test
        fun `should accept jdbc uri with host containing dashes` () {
            // Given
            val jdbcUri = "jdbc:postgres://my-host/databasename"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }


        @Test
        fun `should throw error when port is not a number` () {
            // Given
            val jdbcUri = "jdbc:postgres://host1:port/databasename"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        @DisplayName("should throw error when port is not separated from host by :")
        fun `should throw error when port is wrongly separated from host` () {
            // Given
            val jdbcUri = "jdbc:postgres://host1!8080/databasename"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        fun `should accept jdbc uri with one host and its port` () {
            // Given
            val jdbcUri = "jdbc:postgres://host1:8080/databasename"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }
    }

    @Nested
    inner class DatabaseName {
        @Test
        fun `should throw error when database name and slash are missing` () {
            // Given
            val jdbcUri = "jdbc:postgres//host1:8080"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        fun `should throw error when database name is missing` () {
            // Given
            val jdbcUri = "jdbc:postgres//host1:8080/"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        fun `should throw error when database name contains dot` () {
            // Given
            val jdbcUri = "jdbc:postgres//host1:8080/databasename.collection"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        fun `should accept jdbc uri when database name is present and does not contain dots` () {
            // Given
            val jdbcUri = "jdbc:postgres://host1/databasename"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }
    }

    @Nested
    inner class Options {
        @Test
        @DisplayName("should throw error when option are not formatted like this: name=value")
        fun `should throw error when options are wrongly formatted` () {
            // Given
            val jdbcUri = "jdbc:postgres//host1:8080/databasename?name:value"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        fun `should throw error when missing ? before options` () {
            // Given
            val jdbcUri = "jdbc:postgres//host1:8080/databasenamename=value"

            // When

            // Then
            expectCatching { ConnectionString.buildConnectionString(jdbcUri) }
                    .failed()
                    .isA<IllegalArgumentException>()
        }

        @Test
        fun `should accept jdbc uri when options are correctly formatted` () {
            // Given
            val jdbcUri = "jdbc:postgres://host1/databasename?name=value"

            // When
            val connectionString = ConnectionString.buildConnectionString(jdbcUri)

            // Then
            expectThat(connectionString).isEqualTo(jdbcUri)
        }
    }
}