package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.io.deleteSuspend
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import co.electriccoin.zcash.ui.common.serialization.addressbook.ADDRESS_BOOK_SERIALIZATION_V1
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.IOException
import java.security.GeneralSecurityException

interface LocalAddressBookDataSource {
    suspend fun getContacts(addressBookKey: AddressBookKey): AddressBook

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

    suspend fun saveAddressBook(
        addressBook: AddressBook,
        addressBookKey: AddressBookKey
    )

    suspend fun resetAddressBook()
}

class LocalAddressBookDataSourceImpl(
    private val addressBookStorageProvider: AddressBookStorageProvider,
    private val addressBookProvider: AddressBookProvider
) : LocalAddressBookDataSource {
    private var addressBook: AddressBook? = null

    override suspend fun getContacts(addressBookKey: AddressBookKey): AddressBook =
        withContext(Dispatchers.IO) {
            val addressBook = this@LocalAddressBookDataSourceImpl.addressBook

            if (addressBook == null) {
                var newAddressBook: AddressBook? = readLocalFileToAddressBook(addressBookKey)
                if (newAddressBook == null) {
                    newAddressBook =
                        AddressBook(
                            lastUpdated = Clock.System.now(),
                            version = ADDRESS_BOOK_SERIALIZATION_V1,
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
            val newAddressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = ADDRESS_BOOK_SERIALIZATION_V1,
                    contacts =
                    addressBook?.contacts.orEmpty() +
                        AddressBookContact(
                            name = name,
                            address = address,
                            lastUpdated = lastUpdated,
                        ),
                )
            writeAddressBookToLocalStorage(newAddressBook, addressBookKey)
            newAddressBook
        }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String,
        addressBookKey: AddressBookKey
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            val newAddressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = ADDRESS_BOOK_SERIALIZATION_V1,
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
            writeAddressBookToLocalStorage(newAddressBook, addressBookKey)
            newAddressBook
        }

    override suspend fun deleteContact(
        addressBookContact: AddressBookContact,
        addressBookKey: AddressBookKey
    ): AddressBook =
        withContext(Dispatchers.IO) {
            val lastUpdated = Clock.System.now()
            val newAddressBook =
                AddressBook(
                    lastUpdated = lastUpdated,
                    version = ADDRESS_BOOK_SERIALIZATION_V1,
                    contacts =
                    addressBook?.contacts.orEmpty().toMutableList()
                        .apply {
                            remove(addressBookContact)
                        }
                        .toList(),
                )
            writeAddressBookToLocalStorage(newAddressBook, addressBookKey)
            newAddressBook
        }

    override suspend fun saveAddressBook(
        addressBook: AddressBook,
        addressBookKey: AddressBookKey
    ) {
        writeAddressBookToLocalStorage(addressBook, addressBookKey)
        this.addressBook = addressBook
    }

    override suspend fun resetAddressBook() {
        addressBook = null
    }

    @Suppress("ReturnCount")
    private suspend fun readLocalFileToAddressBook(addressBookKey: AddressBookKey): AddressBook? {
        val encryptedFile = addressBookStorageProvider.getStorageFile(addressBookKey)
        val unencryptedFile = addressBookStorageProvider.getLegacyUnencryptedStorageFile()

        if (encryptedFile != null) {
            return try {
                addressBookProvider.readAddressBookFromFile(encryptedFile, addressBookKey)
                    .also {
                        unencryptedFile?.deleteSuspend()
                    }
            } catch (e: GeneralSecurityException) {
                Twig.warn(e) { "Failed to decrypt address book" }
                null
            } catch (e: IOException) {
                Twig.warn(e) { "Failed to decrypt address book" }
                null
            }
        }

        return if (unencryptedFile != null) {
            addressBookProvider.readLegacyUnencryptedAddressBookFromFile(unencryptedFile)
                .also { unencryptedAddressBook ->
                    writeAddressBookToLocalStorage(unencryptedAddressBook, addressBookKey)
                    unencryptedFile.deleteSuspend()
                }
        } else {
            null
        }
    }

    private fun writeAddressBookToLocalStorage(
        addressBook: AddressBook,
        addressBookKey: AddressBookKey
    ) {
        val file = addressBookStorageProvider.getOrCreateStorageFile(addressBookKey)
        addressBookProvider.writeAddressBookToFile(file, addressBook, addressBookKey)
    }
}
