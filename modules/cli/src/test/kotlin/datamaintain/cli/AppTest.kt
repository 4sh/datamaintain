package datamaintain.cli

import com.github.ajalt.clikt.core.subcommands
import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.step.check.rules.implementations.SameScriptsAsExecutedCheck
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

internal class AppTest {
    data class ConfigWrapper(var datamaintainConfig: DatamaintainConfig? = null)

    private val configWrapper = ConfigWrapper()

    private fun runner(config: DatamaintainConfig) {
        configWrapper.datamaintainConfig = config
    }

    @Test
    fun `should build config with one rule`() {
        // Given
        val argv = getSmallestArgs().plus(listOf(
                "--rule", SameScriptsAsExecutedCheck.NAME
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
            first().isEqualTo(SameScriptsAsExecutedCheck.NAME)
        }
    }

    @Test
    fun `should build config with 2 rules`() {
        // Given
        val argv = getSmallestArgs().plus(listOf(
                "--rule", SameScriptsAsExecutedCheck.NAME,
                "--rule", SameScriptsAsExecutedCheck.NAME
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
            first().isEqualTo(SameScriptsAsExecutedCheck.NAME)
            last().isEqualTo(SameScriptsAsExecutedCheck.NAME)
        }
    }

    @Test
    fun `should build default config without auto override`() {
        // Given
        val configWrapper = ConfigWrapper()

        fun runner(config: DatamaintainConfig) {
            configWrapper.datamaintainConfig = config
        }

        val argv = getSmallestArgs()

        // When
        App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)

        // Then
        defaultChecks(configWrapper)
        expectThat(configWrapper) {
            get { datamaintainConfig }.isNotNull()
        }
        expectThat(configWrapper.datamaintainConfig!!.overrideExecutedScripts) {
            isFalse()
        }
    }

    @Test
    fun `should build config with auto override`() {
        // Given
        val configWrapper = ConfigWrapper()

        fun runner(config: DatamaintainConfig) {
            configWrapper.datamaintainConfig = config
        }

        val argv = getSmallestArgs().plus(listOf(
                "--allow-auto-override"
        ))

        // When
        App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)

        // Then
        defaultChecks(configWrapper)
        expectThat(configWrapper) {
            get { datamaintainConfig }.isNotNull()
        }
        expectThat(configWrapper.datamaintainConfig!!.overrideExecutedScripts) {
            isTrue()
        }
    }

    @Nested
    inner class TrustUri {
        @Test
        fun `should build config with trust uri`() {
            // Given
            val configWrapper = ConfigWrapper()

            fun runner(config: DatamaintainConfig) {
                configWrapper.datamaintainConfig = config
            }

            val argv = listOf(
                    "--trust-uri"
            ).plus(getSmallestArgs())

            // When
            App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)

            // Then
            defaultChecks(configWrapper)
            expectThat(configWrapper) {
                get { datamaintainConfig }.isNotNull()
            }
            expectThat((configWrapper.datamaintainConfig!!.driverConfig as MongoDriverConfig).trustUri) {
                isTrue()
            }
        }

        @Test
        fun `should build config without trust uri`() {
            // Given
            val configWrapper = ConfigWrapper()

            fun runner(config: DatamaintainConfig) {
                configWrapper.datamaintainConfig = config
            }

            val argv = getSmallestArgs()

            // When
            App().subcommands(UpdateDb(runner = ::runner), ListExecutedScripts()).main(argv)

            // Then
            defaultChecks(configWrapper)
            expectThat(configWrapper) {
                get { datamaintainConfig }.isNotNull()
            }
            expectThat((configWrapper.datamaintainConfig!!.driverConfig as MongoDriverConfig).trustUri) {
                isFalse()
            }
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