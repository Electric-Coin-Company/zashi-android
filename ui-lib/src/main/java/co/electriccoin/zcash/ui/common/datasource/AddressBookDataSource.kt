package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.AddressBookContacts
import co.electriccoin.zcash.ui.common.provider.LocalAddressBookProvider
import co.electriccoin.zcash.ui.common.provider.RemoteAddressBookProvider
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface AddressBookDataSource {
    val contacts: Flow<AddressBookContacts?>

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

class AddressBookDataSourceImpl(
    private val localAddressBookProvider: LocalAddressBookProvider,
    private val remoteAddressBookProvider: RemoteAddressBookProvider
) : AddressBookDataSource {
    private val semaphore = Mutex()
    private val contactsCache = MutableStateFlow<AddressBookContacts?>(null)

    override val contacts: Flow<AddressBookContacts?> =
        contactsCache
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
        val local = localAddressBookProvider.saveContact(name, address)
        contactsCache.update { local }
        remoteAddressBookProvider.saveContacts(local)
    }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ) {
        withContext(NonCancellable) {
            ensureSynchronization()
            val local = localAddressBookProvider.updateContact(contact, name, address)
            contactsCache.update { local }
            remoteAddressBookProvider.saveContacts(local)
        }
    }

    override suspend fun deleteContact(contact: AddressBookContact) =
        withNonCancellableSemaphore {
            ensureSynchronization()
            val local = localAddressBookProvider.deleteContact(contact)
            contactsCache.update { local }
            remoteAddressBookProvider.saveContacts(local)
        }

    private suspend fun ensureSynchronization() {
        if (contactsCache.value == null) {
            val merged =
                mergeContacts(
                    local = localAddressBookProvider.getContacts(),
                    remote = remoteAddressBookProvider.getContacts(),
                )

            localAddressBookProvider.saveContacts(merged)
            remoteAddressBookProvider.saveContacts(merged)

            contactsCache.update { merged }
        }
    }

    @Suppress("UnusedParameter")
    private fun mergeContacts(
        local: AddressBookContacts,
        remote: AddressBookContacts?
    ): AddressBookContacts = local

    private suspend fun withNonCancellableSemaphore(block: suspend () -> Unit) {
        withContext(NonCancellable) {
            semaphore.withLock { block() }
        }
    }
}
