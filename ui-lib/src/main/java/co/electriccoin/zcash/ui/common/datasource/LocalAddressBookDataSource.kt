package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

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

class LocalAddressBookDataSourceImpl(
    private val addressBookStorageProvider: AddressBookStorageProvider,
    private val addressBookProvider: AddressBookProvider
) : LocalAddressBookDataSource {
    private var addressBook: AddressBook? = null

    override suspend fun getContacts(): AddressBook =
        withContext(Dispatchers.IO) {
            val addressBook = this@LocalAddressBookDataSourceImpl.addressBook

            if (addressBook == null) {
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
                addressBook
            }
        }

    override suspend fun saveContact(
        name: String,
        address: String
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            addressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = 1,
                    contacts =
                        addressBook?.contacts.orEmpty() +
                            AddressBookContact(
                                name = name,
                                address = address,
                                lastUpdated = lastUpdated,
                            ),
                )
            writeAddressBookToLocalStorage(addressBook!!)
            addressBook!!
        }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            addressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = 1,
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
            writeAddressBookToLocalStorage(addressBook!!)
            addressBook!!
        }

    override suspend fun deleteContact(addressBookContact: AddressBookContact): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            addressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = 1,
                    contacts =
                        addressBook?.contacts.orEmpty().toMutableList()
                            .apply {
                                remove(addressBookContact)
                            }
                            .toList(),
                )
            writeAddressBookToLocalStorage(addressBook!!)
            addressBook!!
        }

    override suspend fun saveContacts(contacts: AddressBook) {
        writeAddressBookToLocalStorage(contacts)
        this@LocalAddressBookDataSourceImpl.addressBook = contacts
    }

    private fun readLocalFileToAddressBook(): AddressBook? {
        val file = addressBookStorageProvider.getStorageFile() ?: return null
        return addressBookProvider.readAddressBookFromFile(file)
    }

    private fun writeAddressBookToLocalStorage(addressBook: AddressBook) {
        val file = addressBookStorageProvider.getOrCreateStorageFile()
        addressBookProvider.writeAddressBookToFile(file, addressBook)
    }
}
