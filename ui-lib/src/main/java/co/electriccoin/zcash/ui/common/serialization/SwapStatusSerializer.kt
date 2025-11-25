package co.electriccoin.zcash.ui.common.serialization

import co.electriccoin.zcash.ui.common.model.SwapStatus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SwapStatusSerializer : KSerializer<SwapStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("SwapStatus", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: SwapStatus
    ) {
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): SwapStatus {
        val value = decoder.decodeString()
        return if (value == "PENDING_DEPOSIT") {
            SwapStatus.PENDING
        } else {
            SwapStatus.entries.firstOrNull { it.value == value } ?: SwapStatus.valueOf(value)
        }
    }
}
