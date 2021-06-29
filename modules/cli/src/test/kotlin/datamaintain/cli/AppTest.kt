package datamaintain.cli

import datamaintain.db.driver.mongo.MongoDriverConfig
import datamaintain.test.execAppInSubprocess
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*

internal class AppTest : BaseCliTest() {
    @Nested
    inner class BaseConfiguration {
        @Nested
        inner class ConfigFilePath {
            @Test
            fun `should build configuration with existing config file path`() {
                // Given
                val argv = listOf("--config-file-path", "src/test/resources/config.properties")

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { mongoUri }
                        .isEqualTo("mongo-uri")
            }

            @Test
            fun `should throw error when specified config file path does not exist`() {
                // Given
                val argv = listOf("--config-file-path", "non-existing-file.properties", "update-db")

                // When
                val (exitCode, output) = execAppInSubprocess(argv)

                // Then
                expectThat(exitCode).isEqualTo(1)
                expectThat(output).contains("java.io.FileNotFoundException: non-existing-file.properties (No such file or directory)")
            }
        }

        @Nested
        inner class DbType {
            @Test
            fun `should build configuration with mongo db type`() {
                // Given
                val argv = listOf("--db-type", datamaintain.cli.DbType.MONGO.value, "--mongo-uri", "mongoUri")

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.driverConfig).isA<MongoDriverConfig>()
            }

            @Test
            fun `should throw error when given db type is not valid`() {
                // Given
                val argv = listOf("--db-type", "invalid db type", "update-db")

                // When
                val (exitCode, output) = execAppInSubprocess(argv)

                // Then
                expectThat(exitCode).isEqualTo(1)
                expectThat(output).contains("dbType invalid db type is unknown")
            }
        }

        @Test
        fun `should build configuration with mongo uri`() {
            // Given
            val mongoUri = "my great mongo uri"
            val argv = listOf("--mongo-uri", mongoUri)

            // When
            runAppWithUpdateDb(argv)

            // Then
            expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                    .get { mongoUri }.isEqualTo(mongoUri)
        }

        @Test
        fun `should build configuration with mongo tmp path`() {
            // Given
            val mongoTmpPath = "my mongo tmp path"
            val argv = listOf("--mongo-uri", "mongouri", "--mongo-tmp-path", mongoTmpPath)

            // When
            runAppWithUpdateDb(argv)

            // Then
            expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                    .get { mongoTmpPath }.isEqualTo(mongoTmpPath)
        }

        @Nested
        inner class TrustUri {
            @Test
            fun `should build config with trust uri`() {
                // Given
                val argv = listOf(
                        "--trust-uri",
                        "--db-type",
                        datamaintain.cli.DbType.MONGO.value,
                        "--mongo-uri",
                        "mongoUri"
                )

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig as MongoDriverConfig).trustUri) {
                    isTrue()
                }
            }

            @Test
            fun `should build config without trust uri`() {
                // Given
                val argv = listOf(
                        "--db-type",
                        datamaintain.cli.DbType.MONGO.value,
                        "--mongo-uri",
                        "mongoUri"
                )

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig as MongoDriverConfig).trustUri) {
                    isFalse()
                }
            }
        }
    }
}