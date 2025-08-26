package co.electriccoin.zcash.ui.common.serialization

import cash.z.ecc.android.sdk.model.Zatoshi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ZatoshiSerializer : KSerializer<Zatoshi> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Zatoshi", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Zatoshi) {
        encoder.encodeLong(value.value)
    }

    override fun deserialize(decoder: Decoder): Zatoshi = Zatoshi(decoder.decodeLong())
}
