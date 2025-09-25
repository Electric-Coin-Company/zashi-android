package co.electriccoin.zcash.ui.common.model.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MetadataSimpleSwapAssetV3Serializer : KSerializer<MetadataSimpleSwapAssetV3> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MetadataSimpleSwapAsset", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: MetadataSimpleSwapAssetV3) {
        encoder.encodeString("${value.token}.${value.chain}")
    }

    override fun deserialize(decoder: Decoder): MetadataSimpleSwapAssetV3 {
        val string = decoder.decodeString()
        val parts = string.split(".")
        return MetadataSimpleSwapAssetV3(token = parts[0], chain = parts[1])
    }
}
