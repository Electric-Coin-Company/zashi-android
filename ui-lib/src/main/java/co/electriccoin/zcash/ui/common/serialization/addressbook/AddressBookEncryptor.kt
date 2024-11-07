package co.electriccoin.zcash.ui.common.serialization.addressbook

import co.electriccoin.zcash.ui.common.model.AddressBook
import com.google.crypto.tink.subtle.ChaCha20Poly1305
import com.google.crypto.tink.subtle.Random
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

internal class AddressBookEncryptor : BaseAddressBookSerializer() {
    fun encryptAddressBook(
        addressBookKey: AddressBookKey,
        serializer: AddressBookSerializer,
        outputStream: OutputStream,
        addressBook: AddressBook
    ) {
        // Generate a fresh one-time key for this ciphertext.
        val salt = Random.randBytes(ADDRESS_BOOK_SALT_SIZE)
        val cipherText =
            ByteArrayOutputStream()
                .use { stream ->
                    serializer.serializeAddressBook(stream, addressBook)
                    stream.toByteArray()
                }.let {
                    val key = addressBookKey.deriveEncryptionKey(salt)
                    // Tink encodes the ciphertext as `nonce || ciphertext || tag`.
                    val cipher = ChaCha20Poly1305.create(key)
                    cipher.encrypt(it, null)
                }

        outputStream.write(ADDRESS_BOOK_ENCRYPTION_V1.createByteArray())
        outputStream.write(salt)
        outputStream.write(cipherText)
    }

    fun decryptAddressBook(
        addressBookKey: AddressBookKey,
        serializer: AddressBookSerializer,
        inputStream: InputStream
    ): AddressBook {
        val version = inputStream.readInt()
        if (version != ADDRESS_BOOK_ENCRYPTION_V1) {
            throw UnknownAddressBookEncryptionVersionException()
        }

        val salt = ByteArray(ADDRESS_BOOK_SALT_SIZE)
        require(inputStream.read(salt) == salt.size) { "Input is too short" }

        val ciphertext = inputStream.readBytes()

        val key = addressBookKey.deriveEncryptionKey(salt)
        val cipher = ChaCha20Poly1305.create(key)
        val plaintext = cipher.decrypt(ciphertext, null)

        return plaintext.inputStream().use { stream ->
            serializer.deserializeAddressBook(stream)
        }
    }
}

class UnknownAddressBookEncryptionVersionException : RuntimeException("Unknown address book encryption version")
