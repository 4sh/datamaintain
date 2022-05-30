package datamaintain.db.driver.mongo

import datamaintain.db.driver.mongo.gson.GsonMapper
import datamaintain.db.driver.mongo.spi.SPI_JSON_MAPPER
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA

internal class GsonScriptMapperTest: JsonMapperTest(GsonMapper()) {
    @Test
    fun `should load SerializationMapper via spi`() {
        expectThat(SPI_JSON_MAPPER).isA<GsonMapper>()
    }
}
