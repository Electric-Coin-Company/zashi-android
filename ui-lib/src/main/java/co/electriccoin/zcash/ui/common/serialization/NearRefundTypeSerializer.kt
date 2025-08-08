package co.electriccoin.zcash.ui.common.serialization

import co.electriccoin.zcash.ui.common.model.near.RefundType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NearRefundTypeSerializer : KSerializer<RefundType?> {
    override val descriptor = PrimitiveSerialDescriptor("RefundType", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: RefundType?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value.apiValue)
    }

    override fun deserialize(decoder: Decoder): RefundType? {
        val decoded = decoder.decodeString()
        return RefundType
            .entries
            .firstOrNull { it.apiValue == decoded }
    }
}
