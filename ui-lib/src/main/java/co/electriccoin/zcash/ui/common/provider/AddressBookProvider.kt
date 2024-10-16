package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import kotlinx.datetime.Instant
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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
        file.outputStream().use {
            serializeAddressBookToByteArrayFile(it, addressBook)
        }
    }

    override fun readAddressBookFromFile(file: File): AddressBook {
        return file.inputStream().use {
            deserializeByteArrayFileToAddressBook(it)
        }
    }

    private fun serializeAddressBookToByteArrayFile(
        outputStream: FileOutputStream,
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

    private fun deserializeByteArrayFileToAddressBook(inputStream: InputStream): AddressBook {
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

    private fun Long.createByteArray(): ByteArray =
        ByteBuffer
            .allocate(Long.SIZE_BYTES).order(BYTE_ORDER).putLong(this).array()

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

private val BYTE_ORDER = ByteOrder.BIG_ENDIAN
