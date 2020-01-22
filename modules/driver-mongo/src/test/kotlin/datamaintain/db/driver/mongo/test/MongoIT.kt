package datamaintain.db.driver.mongo.test

import datamaintain.core.config.DatamaintainConfig
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.report.ReportStatus
import datamaintain.core.runDatamaintain
import datamaintain.core.step.executor.ExecutionMode
import datamaintain.db.driver.mongo.MongoDriverConfig
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
import java.nio.file.Paths

class MongoIT : AbstractMongoDbTest() {
    @Test
    fun `should execute`() {
        // Given
        val config = DatamaintainConfig(
                Paths.get("src/test/resources/integration"),
                Regex("(.*?)_.*"),
                driverConfig = MongoDriverConfig(mongoUri)
        )

        // When
        val executionReport = runDatamaintain(config)

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("01_file.js")
                        get { checksum }.isEqualTo("09a6bb850c3b5c3469af66f0c252d1af")
                        get { identifier }.isEqualTo("01")
                    }
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("02_file.js")
                        get { checksum }.isEqualTo("96d9736617e0a68fc071534b6538b92e")
                        get { identifier }.isEqualTo("02")
                    }
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.OK)
                    get { script }.and {
                        get { name }.isEqualTo("03_file.js")
                        get { checksum }.isEqualTo("9d637bdd8c23f494f4603480c059ebba")
                        get { identifier }.isEqualTo("03")
                    }
                }
            }
        }

        val coll = database.getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(3)
    }

    @Test
    fun `should force mark as executed`() {
        // Given
        val config = DatamaintainConfig(
                Paths.get("src/test/resources/integration"),
                Regex("(.*?)_.*"),
                executionMode = ExecutionMode.FORCE_MARK_AS_EXECUTED,
                driverConfig = MongoDriverConfig(databaseName, mongoUri)
        )

        // When
        val executionReport = runDatamaintain(config,
                true)

        // Then
        expectThat(executionReport) {
            get { status }.isEqualTo(ReportStatus.OK)
            get { lines }.and {
                hasSize(3)
                get(0).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.FORCE_MARKED_AS_EXECUTED)
                    get { script }.and {
                        get { name }.isEqualTo("01_file.js")
                        get { checksum }.isEqualTo("09a6bb850c3b5c3469af66f0c252d1af")
                        get { identifier }.isEqualTo("01")
                    }
                }
                get(1).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.FORCE_MARKED_AS_EXECUTED)
                    get { script }.and {
                        get { name }.isEqualTo("02_file.js")
                        get { checksum }.isEqualTo("96d9736617e0a68fc071534b6538b92e")
                        get { identifier }.isEqualTo("02")
                    }
                }
                get(2).and {
                    get { executionStatus }.isEqualTo(ExecutionStatus.FORCE_MARKED_AS_EXECUTED)
                    get { script }.and {
                        get { name }.isEqualTo("03_file.js")
                        get { checksum }.isEqualTo("9d637bdd8c23f494f4603480c059ebba")
                        get { identifier }.isEqualTo("03")
                    }
                }
            }
        }

        val coll = database.getCollection("simple")
        expectThat(coll.countDocuments()).isEqualTo(0)
    }
}