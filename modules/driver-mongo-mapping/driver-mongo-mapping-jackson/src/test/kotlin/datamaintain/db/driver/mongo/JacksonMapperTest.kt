package datamaintain.db.driver.mongo

import datamaintain.db.driver.mongo.jackson.JacksonMapper
import datamaintain.db.driver.mongo.spi.SPI_JSON_MAPPER
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA


internal class JacksonMapperTest: JsonMapperTest(JacksonMapper()) {
    @Test
    fun `should load SerializationMapper via spi`() {
        expectThat(SPI_JSON_MAPPER).isA<JacksonMapper>()
    }
}
