package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.RemoteAddressBookProvider
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
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
}

class AddressBookRepositoryImpl(
    private val localAddressBookDataSource: LocalAddressBookDataSource,
    private val remoteAddressBookProvider: RemoteAddressBookProvider
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
    ) = withNonCancellableSemaphore {
        ensureSynchronization()
        val local = localAddressBookDataSource.saveContact(name, address)
        addressBookCache.update { local }
        remoteAddressBookProvider.uploadContacts()
    }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ) {
        withNonCancellableSemaphore {
            ensureSynchronization()
            val local = localAddressBookDataSource.updateContact(contact, name, address)
            addressBookCache.update { local }
            remoteAddressBookProvider.uploadContacts()
        }
    }

    override suspend fun deleteContact(contact: AddressBookContact) =
        withNonCancellableSemaphore {
            ensureSynchronization()
            val local = localAddressBookDataSource.deleteContact(contact)
            addressBookCache.update { local }
            remoteAddressBookProvider.uploadContacts()
        }

    private suspend fun ensureSynchronization() {
        if (addressBookCache.value == null) {
            val merged =
                mergeContacts(
                    local = localAddressBookDataSource.getContacts(),
                    remote = remoteAddressBookProvider.fetchContacts(),
                )

            localAddressBookDataSource.saveContacts(merged)
            remoteAddressBookProvider.uploadContacts()

            addressBookCache.update { merged }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mergeContacts(
        local: AddressBook,
        remote: AddressBook?
    ): AddressBook = local // TBD

    private suspend fun withNonCancellableSemaphore(block: suspend () -> Unit) {
        withContext(NonCancellable + Dispatchers.Default) {
            semaphore.withLock { block() }
        }
    }
}
