package co.electriccoin.zcash.ui.common.extension

import cash.z.ecc.android.sdk.type.AddressType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

fun AddressType.toSerializableName(): String =
    when (this) {
        AddressType.Transparent -> "transparent"
        AddressType.Shielded -> "shielded"
        AddressType.Unified -> "unified"
        // Improve this with serializing reason
        is AddressType.Invalid -> "invalid"
    }

fun fromSerializableName(typeName: String): AddressType =
    when (typeName) {
        "transparent" -> AddressType.Transparent
        "shielded" -> AddressType.Shielded
        "unified" -> AddressType.Unified
        // Improve this with deserializing reason
        "invalid" -> AddressType.Invalid()
        else -> error("Unsupported AddressType: $typeName")
    }

object AddressTypeAsStringSerializer : KSerializer<AddressType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AddressType", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: AddressType
    ) {
        val string = value.toSerializableName()
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): AddressType {
        val string = decoder.decodeString()
        return fromSerializableName(string)
    }
}
