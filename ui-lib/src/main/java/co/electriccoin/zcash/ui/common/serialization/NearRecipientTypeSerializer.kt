package co.electriccoin.zcash.ui.common.serialization

import co.electriccoin.zcash.ui.common.model.near.RecipientType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NearRecipientTypeSerializer : KSerializer<RecipientType?> {
    override val descriptor = PrimitiveSerialDescriptor("RecipientType", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: RecipientType?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value.apiValue)
    }

    override fun deserialize(decoder: Decoder): RecipientType? {
        val decoded = decoder.decodeString()
        return RecipientType
            .entries
            .firstOrNull { it.apiValue == decoded }
    }
}
