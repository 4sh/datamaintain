package datamaintain.monitoring

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.format.ConfigurableJackson

val datamaintainJackson = ConfigurableJackson(ObjectMapper())