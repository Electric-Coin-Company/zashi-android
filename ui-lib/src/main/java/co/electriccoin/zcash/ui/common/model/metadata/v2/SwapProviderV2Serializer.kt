package co.electriccoin.zcash.ui.common.model.metadata.v2

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SwapProviderV2Serializer : KSerializer<SwapProviderV2> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("SwapProvider", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SwapProviderV2) {
        encoder.encodeString("${value.provider}.${value.token}.${value.chain}")
    }

    override fun deserialize(decoder: Decoder): SwapProviderV2 {
        val string = decoder.decodeString()
        val parts = string.split(".")
        return SwapProviderV2(
            provider = parts.getOrNull(0).orEmpty(),
            token = parts.getOrNull(1).orEmpty(),
            chain = parts.getOrNull(2).orEmpty(),
        )
    }
}
