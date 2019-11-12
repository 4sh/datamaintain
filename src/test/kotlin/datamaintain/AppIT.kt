package datamaintain

import datamaintain.report.ExecutionStatus
import datamaintain.report.ReportStatus
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.nio.file.Paths

class AppIT: AbstractDbTest() {
    @Test
    fun `should execute`() {
        // Given
        val config = Config(Paths.get("src/test/resources/integration"), databaseName, mongoUri)

        val classUnderTest = Core()

        val scriptPlayed = ScriptWithoutContent("01_file.js", "3dd96b16c91757a3a8a9c2dd09282273")
        config.dbDriver.markAsExecuted(scriptPlayed)

        // When
        val executionReport = classUnderTest.run(config)

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(2)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("02_file.js")
                        get { checksum }.isEqualTo("acadaff7cff05e88fa98d40671040f84")
                    }
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("03_file.js")
                        get { checksum }.isEqualTo("55d3c9ec1e4e65bb2763b05201035d0a")
                    }
                }
            }
        }
    }
}