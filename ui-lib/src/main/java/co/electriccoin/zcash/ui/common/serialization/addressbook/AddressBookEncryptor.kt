package co.electriccoin.zcash.ui.common.serialization.addressbook

import co.electriccoin.zcash.ui.common.model.AddressBook
import com.google.crypto.tink.subtle.ChaCha20Poly1305
import com.google.crypto.tink.subtle.Random
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

internal class AddressBookEncryptor {

    fun encryptAddressBook(
        addressBookKey: AddressBookKey,
        serializer: AddressBookV1Serializer,
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

        outputStream.write(
            ByteBuffer.allocate(Int.SIZE_BYTES)
                .order(ADDRESS_BOOK_BYTE_ORDER)
                .putInt(ADDRESS_BOOK_ENCRYPTION_V1)
                .array()
        )
        outputStream.write(salt)
        outputStream.write(cipherText)
    }

    fun decryptAddressBook(
        addressBookKey: AddressBookKey,
        serializer: AddressBookV1Serializer,
        inputStream: InputStream
    ): AddressBook {
        val version = ByteArray(Int.SIZE_BYTES)
        if (inputStream.read(version) != version.size) {
            throw IllegalArgumentException("input is too short")
        }
        if (ByteBuffer.wrap(version).order(ADDRESS_BOOK_BYTE_ORDER).getInt() != ADDRESS_BOOK_ENCRYPTION_V1) {
            throw RuntimeException("Unknown address book encryption version")
        }

        val salt = ByteArray(ADDRESS_BOOK_SALT_SIZE)
        if (inputStream.read(salt) != salt.size) {
            throw IllegalArgumentException("input is too short")
        }

        val ciphertext = inputStream.readBytes()

        val key = addressBookKey.deriveEncryptionKey(salt)
        val cipher = ChaCha20Poly1305.create(key)
        val plaintext = cipher.decrypt(ciphertext, null)

        return plaintext.inputStream().use { stream ->
            serializer.deserializeAddressBook(stream)
        }
    }
}