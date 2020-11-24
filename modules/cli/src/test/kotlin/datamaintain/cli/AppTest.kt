package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.step.check.rules.implementations.ExecutedScriptsNotRemovedCheck
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class AppTest {
    data class ConfigWrapper(var datamaintainConfig: DatamaintainConfig? = null)

    @Test
    fun `should build config with one rule`() {
        // Given
        val configWrapper = ConfigWrapper()

        fun runner(config: DatamaintainConfig) {
            configWrapper.datamaintainConfig = config
        }

        val argv = getSmallestArgs().plus(listOf(
                "--rule", ExecutedScriptsNotRemovedCheck.NAME
        ))

        // When
        App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)

        // Then
        defaultChecks(configWrapper)
        expectThat(configWrapper) {
            get { datamaintainConfig }.isNotNull()
        }
        expectThat(configWrapper.datamaintainConfig!!.checkRules.toList()) {
            hasSize(1)
            first().isEqualTo(ExecutedScriptsNotRemovedCheck.NAME)
        }
    }

    @Test
    fun `should build config with 2 rules`() {
        // Given
        val configWrapper = ConfigWrapper()

        fun runner(config: DatamaintainConfig) {
            configWrapper.datamaintainConfig = config
        }

        val argv = getSmallestArgs().plus(listOf(
                "--rule", ExecutedScriptsNotRemovedCheck.NAME,
                "--rule", ExecutedScriptsNotRemovedCheck.NAME
        ))

        // When
        App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)

        // Then
        defaultChecks(configWrapper)
        expectThat(configWrapper) {
            get { datamaintainConfig }.isNotNull()
        }
        expectThat(configWrapper.datamaintainConfig!!.checkRules.toList()) {
            hasSize(2)
            first().isEqualTo(ExecutedScriptsNotRemovedCheck.NAME)
            last().isEqualTo(ExecutedScriptsNotRemovedCheck.NAME)
        }
    }

    private fun defaultChecks(configWrapper: ConfigWrapper) {
        expectThat(configWrapper) {
            get { datamaintainConfig }
                    .isNotNull()
                    .get { driverConfig }
                    .isA<MongoDriverConfig>()
                    .get { mongoUri }
                    .isEqualTo("localhost:27017")
            get { datamaintainConfig }
                    .isNotNull()
                    .get { path }
                    .isEqualTo(Paths.get("/tmp"))
        }
    }

    private fun getSmallestArgs(): List<String> {
        return listOf(
                "--db-type", "mongo",
                "--mongo-uri", "localhost:27017",
                "update-db",
                "--path", "/tmp"
        )
    }
}