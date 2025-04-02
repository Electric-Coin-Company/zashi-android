package co.electriccoin.zcash.ui.common.serialization.metada

import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.serialization.BaseSerializer
import co.electriccoin.zcash.ui.common.serialization.METADATA_ENCRYPTION_V1
import co.electriccoin.zcash.ui.common.serialization.METADATA_SALT_SIZE
import co.electriccoin.zcash.ui.common.serialization.UnknownEncryptionVersionException
import com.google.crypto.tink.aead.ChaCha20Poly1305Key
import com.google.crypto.tink.subtle.ChaCha20Poly1305
import com.google.crypto.tink.subtle.Random
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

interface MetadataEncryptor {
    fun encrypt(
        key: MetadataKey,
        outputStream: OutputStream,
        data: Metadata
    )

    @Throws(DecryptionException::class)
    fun decrypt(
        key: MetadataKey,
        inputStream: InputStream
    ): Metadata
}

class MetadataEncryptorImpl(
    private val metadataSerializer: MetadataSerializer,
) : BaseSerializer(),
    MetadataEncryptor {
    private val version: Int = METADATA_ENCRYPTION_V1
    private val saltSize: Int = METADATA_SALT_SIZE

    override fun encrypt(
        key: MetadataKey,
        outputStream: OutputStream,
        data: Metadata
    ) {
        // Generate a fresh one-time key for this ciphertext.
        val salt = Random.randBytes(saltSize)
        val cipherText =
            ByteArrayOutputStream()
                .use { stream ->
                    serialize(stream, data)
                    stream.toByteArray()
                }.let {
                    val derivedKey = key.deriveEncryptionKey(salt)
                    // Tink encodes the ciphertext as `nonce || ciphertext || tag`.
                    val cipher = ChaCha20Poly1305.create(derivedKey)
                    cipher.encrypt(it, null)
                }

        outputStream.write(version.createByteArray())
        outputStream.write(salt)
        outputStream.write(data.version.createByteArray())
        outputStream.write(cipherText)
    }

    override fun decrypt(
        key: MetadataKey,
        inputStream: InputStream
    ): Metadata {
        val version = inputStream.readInt() // read encryption version
        if (version != this.version) {
            throw UnknownEncryptionVersionException()
        }

        val salt = ByteArray(saltSize)
        require(inputStream.read(salt) == salt.size) { "Input is too short" }
        inputStream.readInt() // read metadata version

        val ciphertext = inputStream.readBytes()

        return key
            .deriveDecryptionKeys(salt)
            .asSequence()
            .mapNotNull {
                decrypt(it, ciphertext)
            }.firstOrNull() ?: throw DecryptionException()
    }

    private fun decrypt(
        key: ChaCha20Poly1305Key?,
        ciphertext: ByteArray
    ): Metadata? {
        if (key == null) return null

        return runCatching {
            val cipher = ChaCha20Poly1305.create(key)
            val plaintext = cipher.decrypt(ciphertext, null)
            plaintext.inputStream().use { stream -> deserialize(stream) }
        }.getOrNull()
    }

    private fun serialize(
        outputStream: ByteArrayOutputStream,
        data: Metadata
    ) {
        metadataSerializer.serialize(outputStream, data)
    }

    private fun deserialize(inputStream: ByteArrayInputStream): Metadata = metadataSerializer.deserialize(inputStream)
}

class DecryptionException : Exception()
