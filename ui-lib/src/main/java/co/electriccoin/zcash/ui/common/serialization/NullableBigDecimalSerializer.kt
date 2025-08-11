package co.electriccoin.zcash.ui.common.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

object NullableBigDecimalSerializer : KSerializer<BigDecimal?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NullableBigDecimal", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(
        encoder: Encoder,
        value: BigDecimal?
    ) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value.toPlainString())
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): BigDecimal? =
        if (decoder.decodeNotNullMark()) BigDecimal(decoder.decodeString()) else null
}
