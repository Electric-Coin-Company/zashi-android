package co.electriccoin.zcash.ui.common.serialization

import com.google.crypto.tink.subtle.ChaCha20Poly1305
import com.google.crypto.tink.subtle.Random
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

interface Encryptor<KEY : Key, T> {
    fun encrypt(
        key: KEY,
        outputStream: OutputStream,
        data: T
    )

    fun decrypt(
        key: KEY,
        inputStream: InputStream
    ): T
}

abstract class BaseEncryptor<KEY : Key, T> : BaseSerializer(), Encryptor<KEY, T> {
    abstract val version: Int
    abstract val saltSize: Int

    protected abstract fun serialize(
        outputStream: ByteArrayOutputStream,
        data: T
    )

    protected abstract fun deserialize(inputStream: ByteArrayInputStream): T

    override fun encrypt(
        key: KEY,
        outputStream: OutputStream,
        data: T
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
        outputStream.write(cipherText)
    }

    override fun decrypt(
        key: KEY,
        inputStream: InputStream
    ): T {
        val version = inputStream.readInt()
        if (version != this.version) {
            throw UnknownEncryptionVersionException()
        }

        val salt = ByteArray(saltSize)
        require(inputStream.read(salt) == salt.size) { "Input is too short" }

        val ciphertext = inputStream.readBytes()

        val derivedKey = key.deriveEncryptionKey(salt)
        val cipher = ChaCha20Poly1305.create(derivedKey)
        val plaintext = cipher.decrypt(ciphertext, null)

        return plaintext.inputStream().use { stream ->
            deserialize(stream)
        }
    }
}

class UnknownEncryptionVersionException : RuntimeException("Unknown encryption version")
