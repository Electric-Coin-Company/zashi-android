package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.provider.AddressBookKeyStorageProvider
import co.electriccoin.zcash.ui.common.provider.BlockchainProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface AddressBookRepository {
    val contacts: Flow<List<EnhancedABContact>?>

    suspend fun saveContact(
        name: String,
        address: String,
        chain: String?,
    )

    suspend fun updateContact(
        contact: EnhancedABContact,
        name: String,
        address: String,
        chain: String?,
    )

    suspend fun deleteContact(contact: EnhancedABContact)

    suspend fun resetAddressBook()

    fun observeContactByAddress(address: String): Flow<EnhancedABContact?>
}

data class EnhancedABContact(
    val contact: AddressBookContact,
    val blockchain: SwapAssetBlockchain?
) {
    val name = contact.name
    val address = contact.address
    val lastUpdated = contact.lastUpdated
}

class AddressBookRepositoryImpl(
    private val localAddressBookDataSource: LocalAddressBookDataSource,
    private val addressBookKeyStorageProvider: AddressBookKeyStorageProvider,
    private val accountDataSource: AccountDataSource,
    private val persistableWalletProvider: PersistableWalletProvider,
    private val blockchainProvider: BlockchainProvider
) : AddressBookRepository {
    private val semaphore = Mutex()

    private val addressBookCache = MutableStateFlow<List<EnhancedABContact>?>(null)

    override val contacts: Flow<List<EnhancedABContact>?> =
        addressBookCache
            .onSubscription {
                withNonCancellableSemaphore {
                    ensureSynchronization()
                }
            }

    override suspend fun saveContact(
        name: String,
        address: String,
        chain: String?,
    ) = mutateAddressBook {
        Twig.info { "Address Book: saving a contact" }
        localAddressBookDataSource.saveContact(
            name = name,
            address = address,
            chain = chain,
            addressBookKey = getAddressBookKey()
        )
    }

    override suspend fun updateContact(
        contact: EnhancedABContact,
        name: String,
        address: String,
        chain: String?,
    ) = mutateAddressBook {
        Twig.info { "Address Book: updating a contact" }
        localAddressBookDataSource.updateContact(
            contact = contact.contact,
            name = name,
            address = address,
            chain = chain,
            addressBookKey = getAddressBookKey()
        )
    }

    override suspend fun deleteContact(contact: EnhancedABContact) =
        mutateAddressBook {
            Twig.info { "Address Book: deleting a contact" }
            localAddressBookDataSource.deleteContact(
                addressBookContact = contact.contact,
                addressBookKey = getAddressBookKey()
            )
        }

    override suspend fun resetAddressBook() =
        withNonCancellableSemaphore {
            localAddressBookDataSource.resetAddressBook()
            addressBookCache.update { null }
        }

    override fun observeContactByAddress(address: String): Flow<EnhancedABContact?> =
        contacts
            .filterNotNull()
            .map {
                it.find { contact -> contact.address == address }
            }.distinctUntilChanged()

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
            val contacts =
                addressBook.contacts
                    .map { contact ->
                        EnhancedABContact(
                            contact = contact,
                            blockchain = contact.chain?.let { blockchainProvider.getBlockchain(it) }
                        )
                    }
            addressBookCache.update { contacts }
        }
    }

    private suspend fun mutateAddressBook(block: suspend () -> AddressBook) =
        withNonCancellableSemaphore {
            ensureSynchronization()
            val newAddressBook = block()
            val contacts =
                newAddressBook.contacts
                    .map { contact ->
                        EnhancedABContact(
                            contact = contact,
                            blockchain = contact.chain?.let { blockchainProvider.getBlockchain(it) }
                        )
                    }
            addressBookCache.update { contacts }
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
            val persistableWallet = persistableWalletProvider.requirePersistableWallet()
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
