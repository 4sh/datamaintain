package datamaintain.core.report

import datamaintain.core.db.driver.DatamaintainDriver
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo

internal class ReportTest {
    private val dbDriver = mockk<DatamaintainDriver>()

    @Test
    fun `should do dummy`() {
        expectThat("hello") {
            isEqualTo("world")
        }
    }
}