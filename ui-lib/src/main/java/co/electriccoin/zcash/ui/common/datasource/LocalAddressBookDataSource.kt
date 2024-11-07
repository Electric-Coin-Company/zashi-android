package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.serialization.addressbook.CURRENT_ADDRESS_BOOK_VERSION
import co.electriccoin.zcash.spackle.io.deleteSuspend
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

interface LocalAddressBookDataSource {
    suspend fun getContacts(
        addressBookKey: AddressBookKey
    ): AddressBook

    suspend fun saveContact(
        name: String,
        address: String,
        addressBookKey: AddressBookKey
    ): AddressBook

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String,
        addressBookKey: AddressBookKey
    ): AddressBook

    suspend fun deleteContact(
        addressBookContact: AddressBookContact,
        addressBookKey: AddressBookKey
    ): AddressBook

    suspend fun saveContacts(
        contacts: AddressBook,
        addressBookKey: AddressBookKey
    )

    suspend fun deleteAddressBook(addressBookKey: AddressBookKey)
}

class LocalAddressBookDataSourceImpl(
    private val addressBookStorageProvider: AddressBookStorageProvider,
    private val addressBookProvider: AddressBookProvider
) : LocalAddressBookDataSource {
    private var addressBook: AddressBook? = null

    override suspend fun getContacts(
        addressBookKey: AddressBookKey
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val addressBook = this@LocalAddressBookDataSourceImpl.addressBook

            if (addressBook == null) {
                var newAddressBook: AddressBook? = readLocalFileToAddressBook(addressBookKey)
                if (newAddressBook == null) {
                    newAddressBook =
                        AddressBook(
                            lastUpdated = Clock.System.now(),
                            version = CURRENT_ADDRESS_BOOK_VERSION,
                            contacts = emptyList(),
                        )
                    writeAddressBookToLocalStorage(newAddressBook, addressBookKey)
                }
                newAddressBook
            } else {
                addressBook
            }
        }

    override suspend fun saveContact(
        name: String,
        address: String,
        addressBookKey: AddressBookKey
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            addressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = CURRENT_ADDRESS_BOOK_VERSION,
                    contacts =
                    addressBook?.contacts.orEmpty() +
                        AddressBookContact(
                            name = name,
                            address = address,
                            lastUpdated = lastUpdated,
                        ),
                )
            writeAddressBookToLocalStorage(addressBook!!, addressBookKey)
            addressBook!!
        }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String,
        addressBookKey: AddressBookKey
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            addressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = CURRENT_ADDRESS_BOOK_VERSION,
                    contacts =
                    addressBook?.contacts.orEmpty().toMutableList()
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
            writeAddressBookToLocalStorage(addressBook!!, addressBookKey)
            addressBook!!
        }

    override suspend fun deleteContact(
        addressBookContact: AddressBookContact,
        addressBookKey: AddressBookKey
        ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            addressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = CURRENT_ADDRESS_BOOK_VERSION,
                    contacts =
                    addressBook?.contacts.orEmpty().toMutableList()
                        .apply {
                            remove(addressBookContact)
                        }
                        .toList(),
                )
            writeAddressBookToLocalStorage(addressBook!!, addressBookKey)
            addressBook!!
        }

    override suspend fun saveContacts(contacts: AddressBook, addressBookKey: AddressBookKey) {
        writeAddressBookToLocalStorage(contacts, addressBookKey)
        this@LocalAddressBookDataSourceImpl.addressBook = contacts
    }

    override suspend fun deleteAddressBook(addressBookKey: AddressBookKey) {
        addressBookStorageProvider.getStorageFile(addressBookKey)?.deleteSuspend()
        addressBookStorageProvider.getLegacyUnencryptedStorageFile()?.deleteSuspend()
        addressBook = null
    }

    private suspend fun readLocalFileToAddressBook(addressBookKey: AddressBookKey): AddressBook? {
        addressBookStorageProvider.getStorageFile(addressBookKey)?.let {
            return addressBookProvider.readAddressBookFromFile(it, addressBookKey)
        } ?: addressBookStorageProvider.getLegacyUnencryptedStorageFile()?.let { unencryptedFile ->
            // If we have an unencrypted file, convert it into an encrypted file.
            val addressBook = addressBookProvider.readLegacyUnencryptedAddressBookFromFile(unencryptedFile)
            writeAddressBookToLocalStorage(addressBook, addressBookKey)
            unencryptedFile.deleteSuspend()
            return addressBook
        } ?: return null
    }

    private fun writeAddressBookToLocalStorage(addressBook: AddressBook, addressBookKey: AddressBookKey) {
        val file = addressBookStorageProvider.getOrCreateStorageFile(addressBookKey)
        addressBookProvider.writeAddressBookToFile(file, addressBook, addressBookKey)
    }
}
