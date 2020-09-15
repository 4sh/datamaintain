package datamaintain.db.driver.mongo.serialization

import kotlinx.serialization.*
import java.time.Duration

@Serializer(forClass = Duration::class)
object DurationSerializer: KSerializer<Duration> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("java.time.Duration", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeLong(value.seconds)
    }

    override fun deserialize(decoder: Decoder): Duration {
        return Duration.ofSeconds(decoder.decodeLong())
    }
}