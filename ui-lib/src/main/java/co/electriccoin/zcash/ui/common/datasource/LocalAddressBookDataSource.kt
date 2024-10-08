package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.LocalAddressBookStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface LocalAddressBookDataSource {
    suspend fun getContacts(): AddressBook

    suspend fun saveContact(
        name: String,
        address: String
    ): AddressBook

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ): AddressBook

    suspend fun deleteContact(addressBookContact: AddressBookContact): AddressBook

    suspend fun saveContacts(contacts: AddressBook)
}

@Suppress("TooManyFunctions")
class LocalAddressBookDataSourceImpl(
    private val localAddressBookStorageProvider: LocalAddressBookStorageProvider
) : LocalAddressBookDataSource {
    private var contacts: AddressBook? = null

    override suspend fun getContacts(): AddressBook =
        withContext(Dispatchers.IO) {
            val contacts = this@LocalAddressBookDataSourceImpl.contacts

            if (contacts == null) {
                var newAddressBook: AddressBook? = readLocalFileToAddressBook()
                if (newAddressBook == null) {
                    newAddressBook =
                        AddressBook(
                            lastUpdated = Clock.System.now(),
                            version = 1,
                            contacts = emptyList(),
                        )
                    writeAddressBookToLocalStorage(newAddressBook)
                }
                newAddressBook
            } else {
                contacts
            }
        }

    override suspend fun saveContact(
        name: String,
        address: String
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            contacts =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = 1,
                    contacts =
                        contacts?.contacts.orEmpty() +
                            AddressBookContact(
                                name = name,
                                address = address,
                                lastUpdated = lastUpdated,
                            ),
                )
            writeAddressBookToLocalStorage(contacts!!)
            contacts!!
        }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            contacts =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = 1,
                    contacts =
                        contacts?.contacts.orEmpty().toMutableList()
                            .apply {
                                set(
                                    indexOf(contact),
                                    AddressBookContact(
                                        name = name.trim(),
                                        address = address.trim(),
                                        lastUpdated = Clock.System.now()
                                    )
                                )
                            }
                            .toList(),
                )
            writeAddressBookToLocalStorage(contacts!!)
            contacts!!
        }

    override suspend fun deleteContact(addressBookContact: AddressBookContact): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            contacts =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = 1,
                    contacts =
                        contacts?.contacts.orEmpty().toMutableList()
                            .apply {
                                remove(addressBookContact)
                            }
                            .toList(),
                )
            writeAddressBookToLocalStorage(contacts!!)
            contacts!!
        }

    override suspend fun saveContacts(contacts: AddressBook) {
        writeAddressBookToLocalStorage(contacts)
        this@LocalAddressBookDataSourceImpl.contacts = contacts
    }

    private fun readLocalFileToAddressBook(): AddressBook? {
        return localAddressBookStorageProvider.openStorageInputStream()?.let {
            deserializeByteArrayFileToAddressBook(
                inputStream = it
            )
        }
    }

    private fun writeAddressBookToLocalStorage(addressBook: AddressBook) {
        localAddressBookStorageProvider.openStorageOutputStream()?.let {
            serializeAddressBookToByteArray(
                outputStream = it,
                addressBook = addressBook
            )
        }
    }

    private fun serializeAddressBookToByteArray(
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
