package datamaintain.db.driver.mongo.test

import datamaintain.core.Config
import datamaintain.core.report.ExecutionStatus
import datamaintain.core.report.ReportStatus
import datamaintain.core.runDatamaintain
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.nio.file.Paths

class MongoIT : AbstractMongoDbTest() {
    @Test
    fun `should execute`() {
        // Given
        val config = Config(
                Paths.get("src/test/resources/integration"),
                Regex("(.*?)_.*")
        )

        val driverConfig = MongoDriverConfig(databaseName, mongoUri)

        // When
        val executionReport = runDatamaintain(config, driverConfig)

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("01_file.js")
                        get { checksum }.isEqualTo("3dd96b16c91757a3a8a9c2dd09282273")
                        get { identifier }.isEqualTo("01")
                    }
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("02_file.js")
                        get { checksum }.isEqualTo("acadaff7cff05e88fa98d40671040f84")
                        get { identifier }.isEqualTo("02")
                    }
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("03_file.js")
                        get { checksum }.isEqualTo("55d3c9ec1e4e65bb2763b05201035d0a")
                        get { identifier }.isEqualTo("03")
                    }
                }
            }
        }
    }
}