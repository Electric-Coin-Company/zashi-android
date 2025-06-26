package co.electriccoin.zcash.ui.common.serialization

import co.electriccoin.zcash.ui.common.model.near.SwapType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SwapTypeSerializer : KSerializer<SwapType> {
    override val descriptor = PrimitiveSerialDescriptor("SwapType", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: SwapType
    ) {
        encoder.encodeString(value.apiValue)
    }

    override fun deserialize(decoder: Decoder): SwapType {
        val decoded = decoder.decodeString()
        return SwapType
            .entries
            .first { it.apiValue == decoded }
    }
}
