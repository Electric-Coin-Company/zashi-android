package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataEncryptor
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import java.io.File

interface MetadataProvider {
    fun writeMetadataToFile(
        file: File,
        metadata: Metadata,
        metadataKey: MetadataKey
    )

    fun readMetadataFromFile(
        file: File,
        addressBookKey: MetadataKey
    ): Metadata
}

class MetadataProviderImpl(
    private val metadataEncryptor: MetadataEncryptor
) : MetadataProvider {
    override fun writeMetadataToFile(
        file: File,
        metadata: Metadata,
        metadataKey: MetadataKey
    ) {
        file.outputStream().buffered().use { stream ->
            metadataEncryptor.encrypt(
                key = metadataKey,
                outputStream = stream,
                data = metadata
            )
            stream.flush()
        }
    }

    override fun readMetadataFromFile(
        file: File,
        addressBookKey: MetadataKey
    ): Metadata =
        file.inputStream().use { stream ->
            metadataEncryptor.decrypt(
                key = addressBookKey,
                inputStream = stream
            )
        }
}
