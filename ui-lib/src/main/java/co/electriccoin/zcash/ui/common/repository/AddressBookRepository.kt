package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.AddressBookKeyStorageProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface AddressBookRepository {
    val addressBook: Flow<AddressBook?>

    suspend fun saveContact(
        name: String,
        address: String
    )

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    )

    suspend fun deleteContact(contact: AddressBookContact)

    suspend fun resetAddressBook()
}

class AddressBookRepositoryImpl(
    private val localAddressBookDataSource: LocalAddressBookDataSource,
    private val addressBookKeyStorageProvider: AddressBookKeyStorageProvider,
    private val accountDataSource: AccountDataSource,
    private val persistableWalletProvider: PersistableWalletProvider,
) : AddressBookRepository {
    private val semaphore = Mutex()

    private val addressBookCache = MutableStateFlow<AddressBook?>(null)

    override val addressBook: Flow<AddressBook?> =
        addressBookCache
            .onSubscription {
                withNonCancellableSemaphore {
                    ensureSynchronization()
                }
            }

    override suspend fun saveContact(
        name: String,
        address: String
    ) = mutateAddressBook {
        Twig.info { "Address Book: saving a contact" }
        localAddressBookDataSource.saveContact(
            name = name,
            address = address,
            addressBookKey = getAddressBookKey()
        )
    }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ) = mutateAddressBook {
        Twig.info { "Address Book: updating a contact" }
        localAddressBookDataSource.updateContact(
            contact = contact,
            name = name,
            address = address,
            addressBookKey = getAddressBookKey()
        )
    }

    override suspend fun deleteContact(contact: AddressBookContact) =
        mutateAddressBook {
            Twig.info { "Address Book: deleting a contact" }
            localAddressBookDataSource.deleteContact(
                addressBookContact = contact,
                addressBookKey = getAddressBookKey()
            )
        }

    override suspend fun resetAddressBook() =
        withNonCancellableSemaphore {
            localAddressBookDataSource.resetAddressBook()
            addressBookCache.update { null }
        }

    private suspend fun ensureSynchronization() {
        if (addressBookCache.value == null) {
            val addressBook =
                localAddressBookDataSource.getAddressBook(
                    addressBookKey = getAddressBookKey()
                )
            localAddressBookDataSource.saveAddressBook(
                addressBook = addressBook,
                addressBookKey = getAddressBookKey()
            )
            addressBookCache.update { addressBook }
        }
    }

    private suspend fun mutateAddressBook(block: suspend () -> AddressBook) =
        withNonCancellableSemaphore {
            ensureSynchronization()
            val newAddressBook = block()
            addressBookCache.update { newAddressBook }
        }

    private suspend fun withNonCancellableSemaphore(block: suspend () -> Unit) =
        withContext(NonCancellable + Dispatchers.Default) {
            semaphore.withLock { block() }
        }

    private suspend fun getAddressBookKey(): AddressBookKey {
        val key = addressBookKeyStorageProvider.getAddressBookKey()

        return if (key != null) {
            key
        } else {
            val account = accountDataSource.getZashiAccount()
            val persistableWallet = persistableWalletProvider.getPersistableWallet()
            val newKey =
                AddressBookKey.derive(
                    seedPhrase = persistableWallet.seedPhrase,
                    network = persistableWallet.network,
                    account = account
                )
            addressBookKeyStorageProvider.storeAddressBookKey(newKey)
            newKey
        }
    }
}
