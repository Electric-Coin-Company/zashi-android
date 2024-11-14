package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.Account
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.AddressBookKeyStorageProvider
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
import kotlinx.datetime.Clock
import kotlin.math.max

interface AddressBookRepository {
    val addressBook: Flow<AddressBook?>

    // val googleSignInRequest: Flow<Unit>

    // val googleRemoteConsentRequest: Flow<Intent>

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

    // fun onGoogleSignInSuccess()
    //
    // fun onGoogleSignInCancelled(status: Status?)
    //
    // fun onGoogleSignInError()
}

@Suppress("TooManyFunctions")
class AddressBookRepositoryImpl(
    private val localAddressBookDataSource: LocalAddressBookDataSource,
    private val addressBookKeyStorageProvider: AddressBookKeyStorageProvider,
    private val walletRepository: WalletRepository,
    // private val remoteAddressBookDataSource: RemoteAddressBookDataSource,
    // private val context: Context
) : AddressBookRepository {
    // private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val semaphore = Mutex()

    private val addressBookCache = MutableStateFlow<AddressBook?>(null)

    // private var internalOperation: InternalOperation? = null

    override val addressBook: Flow<AddressBook?> =
        addressBookCache
            .onSubscription {
                withNonCancellableSemaphore {
                    ensureSynchronization()
                }
            }
    // .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(60.seconds), initialValue = null)

    // override val googleSignInRequest = MutableSharedFlow<Unit>()

    // override val googleRemoteConsentRequest = MutableSharedFlow<Intent>()

    // private val internalOperationCompleted = MutableSharedFlow<InternalOperation>()

    override suspend fun saveContact(
        name: String,
        address: String
    ) = withGoogleDrivePermission(InternalOperation.Save(name = name, address = address))

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ) = withGoogleDrivePermission(InternalOperation.Update(contact = contact, name = name, address = address))

    override suspend fun deleteContact(contact: AddressBookContact) =
        withGoogleDrivePermission(
            InternalOperation.Delete(contact = contact)
        )

    override suspend fun resetAddressBook() =
        withNonCancellableSemaphore {
            localAddressBookDataSource.resetAddressBook()
            addressBookCache.update { null }
        }

    // override fun onGoogleSignInSuccess() {
    //     scope.launch {
    //         withNonCancellableSemaphore {
    //             internalOperation?.let {
    //                 Twig.info { "Google sign in success" }
    //                 executeInternalOperation(operation = it)
    //                 this@AddressBookRepositoryImpl.internalOperation = null
    //                 internalOperationCompleted.emit(it)
    //             }
    //         }
    //     }
    // }
    //
    // override fun onGoogleSignInCancelled(status: Status?) {
    //     scope.launch {
    //         withNonCancellableSemaphore {
    //             Twig.info { "Google sign in cancelled, $status" }
    //             internalOperation?.let {
    //                 executeInternalOperation(operation = it)
    //                 this@AddressBookRepositoryImpl.internalOperation = null
    //                 internalOperationCompleted.emit(it)
    //             }
    //         }
    //     }
    // }
    //
    // override fun onGoogleSignInError() {
    //     scope.launch {
    //         withNonCancellableSemaphore {
    //             internalOperation?.let {
    //                 Twig.info { "Address Book: onGoogleSignInError" }
    //                 executeInternalOperation(operation = it)
    //                 this@AddressBookRepositoryImpl.internalOperation = null
    //                 internalOperationCompleted.emit(it)
    //             }
    //         }
    //     }
    // }

    private suspend fun ensureSynchronization(
        forceUpdate: Boolean = false,
        operation: InternalOperation? = null
    ) {
        if (forceUpdate || addressBookCache.value == null) {
            // val remote =
            //     executeRemoteAddressBookSafe {
            //         val contacts = remoteAddressBookDataSource.fetchContacts()
            //         Twig.info { "Address Book: ensureSynchronization - remote address book loaded" }
            //         contacts
            //     }
            val merged =
                mergeContacts(
                    local =
                        localAddressBookDataSource.getContacts(
                            addressBookKey = getAddressBookKey()
                        ),
                    // remote = remote,
                    remote = null,
                    fromOperation = operation
                )
            localAddressBookDataSource.saveAddressBook(
                addressBook = merged,
                addressBookKey = getAddressBookKey()
            )
            // executeRemoteAddressBookSafe {
            //     remoteAddressBookDataSource.uploadContacts()
            //     Twig.info { "Address Book: ensureSynchronization - remote address book uploaded" }
            // }
            addressBookCache.update { merged }
        }
    }

    private fun mergeContacts(
        local: AddressBook,
        remote: AddressBook?,
        fromOperation: InternalOperation?
    ): AddressBook {
        if (remote == null) return local

        val allContacts =
            if (fromOperation is InternalOperation.Delete) {
                (local.contacts + remote.contacts).toMutableList()
                    .apply {
                        removeAll { it.address == fromOperation.contact.address }
                    }
                    .toList()
            } else {
                local.contacts + remote.contacts
            }

        return AddressBook(
            lastUpdated = Clock.System.now(),
            version = max(local.version, remote.version),
            contacts =
                allContacts
                    .groupBy { it.address }
                    .map { (_, contacts) ->
                        contacts.maxBy { it.lastUpdated }
                    }
        )
    }

    private suspend fun withGoogleDrivePermission(internalOperation: InternalOperation) {
        // val remoteConsent = getRemoteConsent()

        // if (hasGoogleDrivePermission() && remoteConsent in
        //     listOf(RemoteConsentResult.HasRemoteConsent, RemoteConsentResult.Error)
        // ) {
        withNonCancellableSemaphore {
            executeInternalOperation(operation = internalOperation)
        }
        // } else {
        //     withNonCancellableSemaphore {
        //         if (remoteConsent is RemoteConsentResult.NoRemoteConsent && remoteConsent.intent != null) {
        //             Twig.info { "Address Book: withGoogleDrivePermission - request consent" }
        //             this.internalOperation = internalOperation
        //             googleRemoteConsentRequest.emit(remoteConsent.intent)
        //         } else {
        //             Twig.info { "Address Book: withGoogleDrivePermission - request permission" }
        //             this.internalOperation = internalOperation
        //             googleSignInRequest.emit(Unit)
        //         }
        //     }
        //     internalOperationCompleted.first { it == internalOperation }
        // }
    }

    // private suspend fun hasGoogleDrivePermission() =
    //     withContext(Dispatchers.IO) {
    //         GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), Scope(GOOGLE_DRIVE_SCOPE))
    //     }

    // private suspend fun getRemoteConsent() = remoteAddressBookDataSource.getRemoteConsent()

    private suspend fun executeInternalOperation(operation: InternalOperation) {
        ensureSynchronization()
        val local =
            when (operation) {
                is InternalOperation.Delete -> {
                    Twig.info { "Address Book: executeInternalOperation - delete" }
                    localAddressBookDataSource.deleteContact(
                        addressBookContact = operation.contact,
                        addressBookKey = getAddressBookKey()
                    )
                }

                is InternalOperation.Save -> {
                    Twig.info { "Address Book: executeInternalOperation - save" }
                    localAddressBookDataSource.saveContact(
                        name = operation.name,
                        address = operation.address,
                        addressBookKey = getAddressBookKey()
                    )
                }

                is InternalOperation.Update -> {
                    Twig.info { "Address Book: executeInternalOperation - update" }
                    localAddressBookDataSource.updateContact(
                        contact = operation.contact,
                        name = operation.name,
                        address = operation.address,
                        addressBookKey = getAddressBookKey()
                    )
                }
            }
        addressBookCache.update { local }
        // scope.launch {
        //     withNonCancellableSemaphore {
        //         ensureSynchronization(forceUpdate = true, operation = operation)
        //     }
        // }
    }

    private suspend fun withNonCancellableSemaphore(block: suspend () -> Unit) {
        withContext(NonCancellable + Dispatchers.Default) {
            semaphore.withLock { block() }
        }
    }

    private suspend fun getAddressBookKey(): AddressBookKey {
        val key = addressBookKeyStorageProvider.getAddressBookKey()

        return if (key != null) {
            key
        } else {
            val wallet = walletRepository.getPersistableWallet()
            val newKey =
                AddressBookKey.derive(
                    seedPhrase = wallet.seedPhrase,
                    network = wallet.network,
                    account = Account.DEFAULT
                )
            addressBookKeyStorageProvider.storeAddressBookKey(newKey)
            newKey
        }
    }

    // @Suppress("TooGenericExceptionCaught")
    // private suspend fun <T> executeRemoteAddressBookSafe(block: suspend () -> T): T? {
    //     if (hasGoogleDrivePermission().not()) {
    //         return null
    //     }
    //
    //     return try {
    //         block()
    //     } catch (e: UserRecoverableAuthException) {
    //         Twig.error(e) { "Address Book: remote execution failed" }
    //         null
    //     } catch (e: UserRecoverableAuthIOException) {
    //         Twig.error(e) { "Address Book: remote execution failed" }
    //         null
    //     } catch (e: GoogleAuthException) {
    //         Twig.error(e) { "Address Book: remote execution failed" }
    //         null
    //     } catch (e: GoogleJsonResponseException) {
    //         Twig.error(e) { "Address Book: remote execution failed" }
    //         null
    //     } catch (e: IOException) {
    //         Twig.error(e) { "Address Book: remote execution failed" }
    //         null
    //     } catch (e: IllegalArgumentException) {
    //         Twig.error(e) { "Address Book: remote execution failed" }
    //         null
    //     } catch (e: Exception) {
    //         Twig.error(e) { "Address Book: remote execution failed" }
    //         null
    //     }
    // }
}

private sealed interface InternalOperation {
    data class Save(val name: String, val address: String) : InternalOperation

    data class Update(val contact: AddressBookContact, val name: String, val address: String) : InternalOperation

    data class Delete(val contact: AddressBookContact) : InternalOperation
}

// private const val GOOGLE_DRIVE_SCOPE = Scopes.DRIVE_APPFOLDER
