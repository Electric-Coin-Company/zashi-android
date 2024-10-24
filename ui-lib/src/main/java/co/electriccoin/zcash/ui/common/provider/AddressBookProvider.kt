package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.aead.ChaCha20Poly1305Key
import com.google.crypto.tink.subtle.ChaCha20Poly1305
import com.google.crypto.tink.subtle.Hkdf
import com.google.crypto.tink.subtle.Random
import com.google.crypto.tink.util.SecretBytes
import kotlinx.datetime.Instant
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface AddressBookProvider {
    fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook
    )

    fun readAddressBookFromFile(file: File): AddressBook
}

class AddressBookProviderImpl : AddressBookProvider {
    override fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook
    ) {
        // TODO: Fetch AddressBookKey from keystore.
        val addressBookKey = 0
        encryptAddressBook(addressBookKey, file.outputStream(), addressBook)
    }

    override fun readAddressBookFromFile(file: File): AddressBook {
        // TODO: Fetch AddressBookKey from keystore.
        val addressBookKey = 0
        return decryptAddressBook(addressBookKey, file.inputStream())
    }

    /**
     * Serializes the given address book and then encrypts it for the given account.
     *
     * The ciphertext is written to the provided output stream.
     */
    private fun encryptAddressBook(
        addressBookKey: AddressBookKey,
        outputStream: OutputStream,
        addressBook: AddressBook
    ) {
        // Generate a fresh one-time key for this ciphertext.
        val salt = Random.randBytes(32)
        val key = addressBookKey.deriveEncryptionKey(salt)

        val plaintext =
            ByteArrayOutputStream().use { stream ->
                serializeAddressBook(stream, addressBook)
                stream.toByteArray()
            }

        // Tink encodes the ciphertext as `nonce || ciphertext || tag`.
        val cipher = ChaCha20Poly1305.create(key)
        val ciphertext = cipher.encrypt(plaintext, null)

        outputStream.buffered().use {
            it.write(salt)
            it.write(ciphertext)
        }
    }

    /**
     * Attempts to decrypt the input stream as an address book using the given
     * key.
     */
    private fun decryptAddressBook(
        addressBookKey: AddressBookKey,
        inputStream: InputStream
    ): AddressBook {
        val plaintext =
            inputStream.buffered().use { stream ->
                val salt = ByteArray(32)
                if (stream.read(salt) != salt.size) {
                    throw IllegalArgumentException("input is too short")
                }

                val ciphertext = stream.readBytes()

                val key = addressBookKey.deriveEncryptionKey(salt)
                val cipher = ChaCha20Poly1305.create(key)
                cipher.decrypt(ciphertext, null)
            }

        return deserializeAddressBook(plaintext.inputStream())
    }

    private fun serializeAddressBook(
        outputStream: OutputStream,
        addressBook: AddressBook
    ) {
        outputStream.buffered().use {
            it.write(addressBook.version.createByteArray())
            it.write(addressBook.lastUpdated.toEpochMilliseconds().createByteArray())
            it.write(addressBook.contacts.size.createByteArray())

            addressBook.contacts.forEach { contact ->
                it.write(contact.lastUpdated.toEpochMilliseconds().createByteArray())
                it.write(contact.address.createByteArray())
                it.write(contact.name.createByteArray())
            }
        }
    }

    private fun deserializeAddressBook(inputStream: InputStream): AddressBook {
        return inputStream.buffered().use { stream ->
            AddressBook(
                version = stream.readInt(),
                lastUpdated = stream.readLong().let { Instant.fromEpochMilliseconds(it) },
                contacts =
                    stream.readInt().let { contactsSize ->
                        (0 until contactsSize).map { _ ->
                            AddressBookContact(
                                lastUpdated = stream.readLong().let { Instant.fromEpochMilliseconds(it) },
                                address = stream.readString(),
                                name = stream.readString(),
                            )
                        }
                    }
            )
        }
    }

    private fun Int.createByteArray(): ByteArray = this.toLong().createByteArray()

    private fun Long.createByteArray(): ByteArray = ByteBuffer.allocate(Long.SIZE_BYTES).order(BYTE_ORDER).putLong(this).array()

    private fun String.createByteArray(): ByteArray {
        val byteArray = this.toByteArray()
        return byteArray.size.createByteArray() + byteArray
    }

    private fun InputStream.readInt(): Int = readLong().toInt()

    private fun InputStream.readLong(): Long {
        val buffer = ByteArray(Long.SIZE_BYTES)
        this.read(buffer)
        return ByteBuffer.wrap(buffer).order(BYTE_ORDER).getLong()
    }

    private fun InputStream.readString(): String {
        val size = this.readInt()
        val buffer = ByteArray(size)
        this.read(buffer)
        return String(buffer)
    }
}

/**
 * The long-term key that can decrypt an account's encrypted address book.
 */
class AddressBookKey(val key: SecretBytes) {
    /**
     * Derives a one-time address book encryption key.
     *
     * At encryption time, the one-time property MUST be ensured by generating a
     * random 32-byte salt.
     */
    fun deriveEncryptionKey(salt: ByteArray): ChaCha20Poly1305Key {
        assert(salt.size == 32)
        val access = InsecureSecretKeyAccess.get()
        val subKey = Hkdf.computeHkdf("HMACSHA256", key.toByteArray(access), salt, null, 32)
        return ChaCha20Poly1305Key.create(SecretBytes.copyFrom(subKey, access))
    }

    companion object {
        /**
         * Derives the long-term key that can decrypt the given account's encrypted
         * address book.
         *
         * This requires access to the seed phrase. If the app has separate access
         * control requirements for the seed phrase and the address book, this key
         * should be cached in the app's keystore.
         */
        suspend fun derive(
            seedPhrase: SeedPhrase,
            network: ZcashNetwork,
            account: Account
        ): AddressBookKey {
            val key =
                DerivationTool.getInstance().deriveArbitraryAccountKey(
                    "ZashiAddressBookEncryptionV1".toByteArray(),
                    seedPhrase.toByteArray(),
                    network,
                    account,
                )
            return AddressBookKey(SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get()))
        }
    }
}

private val BYTE_ORDER = ByteOrder.BIG_ENDIAN
