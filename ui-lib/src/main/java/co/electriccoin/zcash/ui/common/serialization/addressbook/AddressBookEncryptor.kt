package co.electriccoin.zcash.ui.common.serialization.addressbook

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.serialization.ADDRESS_BOOK_ENCRYPTION_V1
import co.electriccoin.zcash.ui.common.serialization.ADDRESS_BOOK_SALT_SIZE
import co.electriccoin.zcash.ui.common.serialization.BaseSerializer
import co.electriccoin.zcash.ui.common.serialization.UnknownEncryptionVersionException
import com.google.crypto.tink.subtle.ChaCha20Poly1305
import com.google.crypto.tink.subtle.Random
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

interface AddressBookEncryptor {
    fun encrypt(
        key: AddressBookKey,
        outputStream: OutputStream,
        data: AddressBook
    )

    fun decrypt(
        key: AddressBookKey,
        inputStream: InputStream
    ): AddressBook
}

class AddressBookEncryptorImpl(
    private val addressBookSerializer: AddressBookSerializer,
) : BaseSerializer(),
    AddressBookEncryptor {
    private val version: Int = ADDRESS_BOOK_ENCRYPTION_V1
    private val saltSize: Int = ADDRESS_BOOK_SALT_SIZE

    override fun encrypt(
        key: AddressBookKey,
        outputStream: OutputStream,
        data: AddressBook
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
        key: AddressBookKey,
        inputStream: InputStream
    ): AddressBook {
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

    private fun serialize(
        outputStream: ByteArrayOutputStream,
        data: AddressBook
    ) {
        addressBookSerializer.serializeAddressBook(outputStream, data)
    }

    private fun deserialize(inputStream: ByteArrayInputStream): AddressBook =
        addressBookSerializer.deserializeAddressBook(inputStream)
}
