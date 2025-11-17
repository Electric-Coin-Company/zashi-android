package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.io.deleteSuspend
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface AddressBookDataSource {
    fun observe(key: AddressBookKey): Flow<AddressBook?>

    suspend fun saveContact(
        name: String,
        address: String,
        chain: String?,
        key: AddressBookKey
    )

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String,
        chain: String?,
        key: AddressBookKey
    )

    suspend fun deleteContact(addressBookContact: AddressBookContact, key: AddressBookKey)

    suspend fun delete(key: AddressBookKey)
}

class AddressBookDataSourceImpl(
    private val addressBookStorageProvider: AddressBookStorageProvider,
    private val addressBookProvider: AddressBookProvider
) : AddressBookDataSource {
    private val mutex = Mutex()

    private val abUpdatePipeline = MutableSharedFlow<Pair<AddressBookKey, AddressBook?>>()

    override fun observe(key: AddressBookKey) =
        flow {
            emit(null)
            mutex.withLock { emit(getAddressBookInternal(key)) }
            abUpdatePipeline.collect { (newKey, newAddressBook) ->
                if (key.key.equalsSecretBytes(newKey.key)) {
                    emit(newAddressBook)
                }
            }
        }.distinctUntilChanged()

    override suspend fun saveContact(
        name: String,
        address: String,
        chain: String?,
        key: AddressBookKey
    ) = updateAB(key) { contacts ->
        contacts +
            AddressBookContact(
                name = name.trim(),
                address = address.trim(),
                chain = chain?.trim()?.takeIf { it.isNotEmpty() },
                lastUpdated = getTimestampNow(),
            )
    }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String,
        chain: String?,
        key: AddressBookKey
    ) = updateAB(key) { contacts ->
        contacts.apply {
            set(
                indexOf(contact),
                AddressBookContact(
                    name = name.trim(),
                    address = address.trim(),
                    chain = chain?.trim()?.takeIf { it.isNotEmpty() },
                    lastUpdated = getTimestampNow(),
                )
            )
        }
    }

    override suspend fun deleteContact(addressBookContact: AddressBookContact, key: AddressBookKey) =
        updateAB(key) { it.apply { remove(addressBookContact) } }

    override suspend fun delete(key: AddressBookKey) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                addressBookStorageProvider.getStorageFile(key)?.delete()
                abUpdatePipeline.emit(key to null)
            }
        }
    }

    @Suppress("ReturnCount")
    private suspend fun getAddressBookInternal(addressBookKey: AddressBookKey): AddressBook {
        suspend fun readLocalFileToAddressBook(addressBookKey: AddressBookKey): AddressBook? {
            val encryptedFile = runCatching { addressBookStorageProvider.getStorageFile(addressBookKey) }.getOrNull()
            val unencryptedFile =
                runCatching { addressBookStorageProvider.getLegacyUnencryptedStorageFile() }.getOrNull()

            return when {
                encryptedFile != null ->
                    runCatching {
                        addressBookProvider
                            .readAddressBookFromFile(encryptedFile, addressBookKey)
                            .also { unencryptedFile?.deleteSuspend() }
                    }.onFailure { e -> Twig.warn(e) { "Failed to decrypt address book" } }.getOrNull()

                unencryptedFile != null ->
                    addressBookProvider
                        .readLegacyUnencryptedAddressBookFromFile(unencryptedFile)
                        .also { unencryptedAddressBook ->
                            writeToLocalStorage(unencryptedAddressBook, addressBookKey)
                            unencryptedFile.deleteSuspend()
                        }

                else -> null
            }
        }

        return withContext(Dispatchers.IO) {
            var addressBook = readLocalFileToAddressBook(addressBookKey)
            if (addressBook == null) {
                addressBook = AddressBook(lastUpdated = getTimestampNow(), contacts = emptyList())
                writeToLocalStorage(addressBook, addressBookKey)
            }
            addressBook
        }
    }

    private fun writeToLocalStorage(addressBook: AddressBook, key: AddressBookKey) {
        runCatching {
            val file = addressBookStorageProvider.getOrCreateStorageFile(key)
            addressBookProvider.writeAddressBookToFile(file, addressBook, key)
        }.onFailure { e -> Twig.warn(e) { "Failed to write address book" } }
    }

    private suspend fun updateAB(
        key: AddressBookKey,
        transform: (MutableList<AddressBookContact>) -> List<AddressBookContact>
    ) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val addressBook = getAddressBookInternal(key)
            val newAddressBook =
                AddressBook(
                    lastUpdated = getTimestampNow(),
                    contacts = transform(addressBook.contacts.toMutableList()).toList(),
                )
            writeToLocalStorage(newAddressBook, key)
            abUpdatePipeline.emit(key to newAddressBook)
        }
    }

    private fun getTimestampNow(): Instant =
        Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
}
