package co.electriccoin.zcash.ui.common.serialization

import co.electriccoin.zcash.ui.common.model.SwapProvider
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SwapProviderSerializer : KSerializer<SwapProvider> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("SwapProvider", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: SwapProvider
    ) {
        encoder.encodeString(
            "${value.provider}.${value.token}.${value.chain}"
        )
    }

    override fun deserialize(decoder: Decoder): SwapProvider {
        val string = decoder.decodeString()
        val parts = string.split(".")
        return SwapProvider(
            provider = parts.getOrNull(0).orEmpty(),
            token = parts.getOrNull(1).orEmpty(),
            chain = parts.getOrNull(2).orEmpty(),
        )
    }
}
