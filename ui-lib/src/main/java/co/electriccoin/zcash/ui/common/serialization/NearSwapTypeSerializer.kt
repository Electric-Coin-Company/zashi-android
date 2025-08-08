package co.electriccoin.zcash.ui.common.serialization

import co.electriccoin.zcash.ui.common.model.near.SwapType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NearSwapTypeSerializer : KSerializer<SwapType?> {
    override val descriptor = PrimitiveSerialDescriptor("SwapType", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: SwapType?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value.apiValue)
    }

    override fun deserialize(decoder: Decoder): SwapType? {
        val decoded = decoder.decodeString()
        return SwapType
            .entries
            .firstOrNull { it.apiValue == decoded }
    }
}
