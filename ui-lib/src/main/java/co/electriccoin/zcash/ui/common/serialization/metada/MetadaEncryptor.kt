package co.electriccoin.zcash.ui.common.serialization.metada

import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.serialization.BaseEncryptor
import co.electriccoin.zcash.ui.common.serialization.Encryptor
import co.electriccoin.zcash.ui.common.serialization.METADATA_ENCRYPTION_V1
import co.electriccoin.zcash.ui.common.serialization.METADATA_SALT_SIZE
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

interface MetadataEncryptor : Encryptor<MetadataKey, Metadata>

class MetadataEncryptorImpl(
    private val metadataSerializer: MetadataSerializer,
) : MetadataEncryptor, BaseEncryptor<MetadataKey, Metadata>() {
    override val version: Int = METADATA_ENCRYPTION_V1
    override val saltSize: Int = METADATA_SALT_SIZE

    override fun serialize(
        outputStream: ByteArrayOutputStream,
        data: Metadata
    ) {
        metadataSerializer.serialize(outputStream, data)
    }

    override fun deserialize(inputStream: ByteArrayInputStream): Metadata = metadataSerializer.deserialize(inputStream)
}

class UnknownEncryptionVersionException : RuntimeException("Unknown encryption version")
