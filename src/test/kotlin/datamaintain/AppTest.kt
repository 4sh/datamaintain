package datamaintain

import org.junit.jupiter.api.Test
import strikt.api.*
import strikt.assertions.*

class AppTest {
    @Test
    fun `should have a greeting`() {
        val classUnderTest = App()

        expectThat(classUnderTest.greeting).isNotNull().and {
            startsWith("Hello")
            contains("world")
        }
    }
}
