package co.electriccoin.zcash.ui.common.serialization

import co.electriccoin.zcash.ui.common.model.near.RefundType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object RefundTypeSerializer : KSerializer<RefundType> {
    override val descriptor = PrimitiveSerialDescriptor("RefundType", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: RefundType
    ) {
        encoder.encodeString(value.apiValue)
    }

    override fun deserialize(decoder: Decoder): RefundType = RefundType
        .entries
        .first { it.apiValue == decoder.decodeString() }
}
