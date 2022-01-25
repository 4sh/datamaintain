package datamaintain.cli.app

import datamaintain.db.driver.jdbc.JdbcDriverConfig
import datamaintain.db.driver.mongo.MongoDriverConfig
import datamaintain.test.execAppInSubprocess
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*

internal class AppTest : BaseCliTest() {
    private fun buildPathToConfigFile(fileName: String): String = "src/test/resources/${fileName}.properties"

    private val configFilePath = buildPathToConfigFile("config")

    private val configWithoutDbTypePath = buildPathToConfigFile("config-without-db-type")

    @Nested
    inner class BaseConfiguration {
        @Nested
        inner class ConfigFilePath {
            @Test
            fun `should build configuration with existing config file path`() {
                // Given
                val argv = listOf("--config-file-path", configFilePath)

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { uri }
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

            @Test
            fun `should build configuration from absolute working directory`() {
                // Given
                val argv = listOf(
                        "--working-directory-path", System.getProperty("user.dir") + "/src/test/resources/",
                        "--config-file-path", "config.properties")

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { uri }
                        .isEqualTo("mongo-uri")
            }

            @Test
            fun `should build configuration from relative working directory`() {
                // Given
                val argv = listOf(
                        "--working-directory-path", "src/test/resources/",
                        "--config-file-path", "config.properties")

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat((configWrapper.datamaintainConfig!!.driverConfig) as MongoDriverConfig)
                        .get { uri }
                        .isEqualTo("mongo-uri")
            }
        }

        @Nested
        inner class DbType {
            @Test
            fun `should build configuration with mongo db type`() {
                // Given
                val argv = listOf("--db-type", datamaintain.cli.app.DbType.MONGO.value, "--db-uri", "mongoUri")

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.driverConfig).isA<MongoDriverConfig>()
            }

            @Test
            fun `should build configuration with jdbc db type`() {
                // Given
                val argv = listOf("--db-type", datamaintain.cli.app.DbType.JDBC.value, "--db-uri", "jdbcUri")

                // When
                runAppWithUpdateDb(argv)

                // Then
                expectThat(configWrapper.datamaintainConfig!!.driverConfig).isA<JdbcDriverConfig>()
            }

            @Test
            fun `should throw error when given db type is not valid`() {
                // Given
                val argv = listOf("--db-type", "invalid db type", "update-db")

                // When
                val (exitCode, output) = execAppInSubprocess(argv)

                // Then
                expectThat(exitCode).isEqualTo(1)
                expectThat(output).contains("Invalid value for \"--db-type\": invalid choice: invalid db type.")
            }

            @Test
            fun `should throw error when no db type was provided`() {
                // Given
                val argv = listOf("--config-file-path", configWithoutDbTypePath, "update-db")

                // When
                val (exitCode, output) = execAppInSubprocess(argv)

                // Then
                expectThat(exitCode).isEqualTo(1)
                expectThat(output).contains("props.getProperty(\"db.type\") must not be null")
            }
        }

        @Test
        fun `should build configuration with mongo uri`() {
            // Given
            val mongoUri = "my great mongo uri"
            val argv = listOf("--db-type", "mongo", "--db-uri", mongoUri)

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
            val argv = listOf("--db-type", "mongo", "--db-uri", "mongouri", "--mongo-tmp-path", mongoTmpPath)

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
                        datamaintain.cli.app.DbType.MONGO.value,
                        "--db-uri",
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
                        datamaintain.cli.app.DbType.MONGO.value,
                        "--db-uri",
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
