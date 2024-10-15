@file:Suppress("DEPRECATION")

package co.electriccoin.zcash.ui.common.repository

import android.content.Context
import android.content.Intent
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.RemoteAddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.RemoteAddressBookDataSource.RemoteConsentResult
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.api.Status
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException

interface AddressBookRepository {
    val addressBook: Flow<AddressBook?>

    val googleSignInRequest: Flow<Unit>

    val googleRemoteConsentRequest: Flow<Intent>

    suspend fun saveContact(name: String, address: String)

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    )

    suspend fun deleteContact(contact: AddressBookContact)

    fun onGoogleSignInSuccess()

    fun onGoogleSignInCancelled(status: Status?)

    fun onGoogleSignInError()
}

class AddressBookRepositoryImpl(
    private val localAddressBookDataSource: LocalAddressBookDataSource,
    private val remoteAddressBookDataSource: RemoteAddressBookDataSource,
    private val context: Context
) : AddressBookRepository {

    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val semaphore = Mutex()

    private val addressBookCache = MutableStateFlow<AddressBook?>(null)

    private var internalOperation: InternalOperation? = null

    override val addressBook: Flow<AddressBook?> =
        addressBookCache
            .onSubscription {
                withNonCancellableSemaphore {
                    ensureSynchronization()
                }
            }

    override val googleSignInRequest = MutableSharedFlow<Unit>()
    override val googleRemoteConsentRequest = MutableSharedFlow<Intent>()

    private val internalOperationCompleted = MutableSharedFlow<InternalOperation>()

    override suspend fun saveContact(name: String, address: String) =
        withGoogleDrivePermission(InternalOperation.Save(name = name, address = address))

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ) = withGoogleDrivePermission(InternalOperation.Update(contact = contact, name = name, address = address))

    override suspend fun deleteContact(contact: AddressBookContact) =
        withGoogleDrivePermission(InternalOperation.Delete(contact = contact))

    override fun onGoogleSignInSuccess() {
        scope.launch {
            withNonCancellableSemaphore {
                internalOperation?.let {
                    Twig.info { "Google sign in success" }
                    try {
                        executeInternalOperation(operation = it, throwRemoteConsentExceptions = true)
                        this@AddressBookRepositoryImpl.internalOperation = null
                        internalOperationCompleted.emit(it)
                    } catch (e: UserRecoverableAuthIOException) {
                        e.intent?.let { intent ->
                            googleRemoteConsentRequest.emit(intent)
                        }
                    } catch (e: UserRecoverableAuthException) {
                        e.intent?.let { intent ->
                            googleRemoteConsentRequest.emit(intent)
                        }
                    }
                }
            }
        }
    }

    override fun onGoogleSignInCancelled(status: Status?) {
        scope.launch {
            withNonCancellableSemaphore {
                Twig.info { "Google sign in cancelled, $status" }
                internalOperation?.let {
                    executeInternalOperation(operation = it, throwRemoteConsentExceptions = false)
                    this@AddressBookRepositoryImpl.internalOperation = null
                    internalOperationCompleted.emit(it)
                }
            }
        }
    }

    override fun onGoogleSignInError() {
        scope.launch {
            withNonCancellableSemaphore {
                internalOperation?.let {
                    Twig.info { "Address Book: onGoogleSignInError" }
                    executeInternalOperation(operation = it, throwRemoteConsentExceptions = false)
                    this@AddressBookRepositoryImpl.internalOperation = null
                    internalOperationCompleted.emit(it)
                }
            }
        }
    }

    private suspend fun ensureSynchronization() {
        if (addressBookCache.value == null) {
            val remote = executeRemoteAddressBookSafe(throwRemoteConsentExceptions = false) {
                val contacts = remoteAddressBookDataSource.fetchContacts()
                Twig.info { "Address Book: ensureSynchronization - remote address book loaded" }
                contacts
            }
            val merged =
                mergeContacts(
                    local = localAddressBookDataSource.getContacts(),
                    remote = remote,
                )
            localAddressBookDataSource.saveContacts(merged)
            executeRemoteAddressBookSafe(throwRemoteConsentExceptions = false) {
                remoteAddressBookDataSource.uploadContacts()
                Twig.info { "Address Book: ensureSynchronization - remote address book uploaded" }
            }
            addressBookCache.update { merged }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mergeContacts(local: AddressBook, remote: AddressBook?): AddressBook = local // TBD

    private suspend fun withGoogleDrivePermission(internalOperation: InternalOperation) {
        val remoteConsent = getRemoteConsent()

        if (hasGoogleDrivePermission() && remoteConsent is RemoteConsentResult.HasRemoteConsent) {
            try {
                withNonCancellableSemaphore {
                    executeInternalOperation(operation = internalOperation, throwRemoteConsentExceptions = true)
                }
            } catch (e: UserRecoverableAuthIOException) {
                this@AddressBookRepositoryImpl.internalOperation = internalOperation
                e.intent?.let {
                    googleRemoteConsentRequest.emit(it)
                }
                internalOperationCompleted.first { it == internalOperation }
            } catch (e: UserRecoverableAuthException) {
                this@AddressBookRepositoryImpl.internalOperation = internalOperation
                e.intent?.let {
                    googleRemoteConsentRequest.emit(it)
                }
                internalOperationCompleted.first { it == internalOperation }
            }
        } else {
            withNonCancellableSemaphore {
                if (remoteConsent is RemoteConsentResult.NoRemoteConsent && remoteConsent.intent != null) {
                    Twig.info { "Address Book: withGoogleDrivePermission - request consent" }
                    this.internalOperation = internalOperation
                    googleRemoteConsentRequest.emit(remoteConsent.intent)
                } else {
                    Twig.info { "Address Book: withGoogleDrivePermission - request permission" }
                    this.internalOperation = internalOperation
                    googleSignInRequest.emit(Unit)
                }


            }
            internalOperationCompleted.first { it == internalOperation }
        }
    }

    private suspend fun hasGoogleDrivePermission() = withContext(Dispatchers.IO) {
        GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), Scope(GOOGLE_DRIVE_SCOPE))
    }

    private suspend fun getRemoteConsent() = remoteAddressBookDataSource.getRemoteConsent()

    /**
     * @throws [UserRecoverableAuthException] or [UserRecoverableAuthIOException] if a consent error occurred and
     * throwRemoteConsentExceptions is true
     */
    @Throws(UserRecoverableAuthException::class, UserRecoverableAuthIOException::class)
    private suspend fun executeInternalOperation(operation: InternalOperation, throwRemoteConsentExceptions: Boolean) {
        ensureSynchronization()
        val local = when (operation) {
            is InternalOperation.Delete -> {
                Twig.info { "Address Book: executeInternalOperation - delete" }
                localAddressBookDataSource.deleteContact(addressBookContact = operation.contact)
            }

            is InternalOperation.Save -> {
                Twig.info { "Address Book: executeInternalOperation - save" }
                localAddressBookDataSource.saveContact(name = operation.name, address = operation.address)
            }

            is InternalOperation.Update -> {
                Twig.info { "Address Book: executeInternalOperation - update" }
                localAddressBookDataSource.updateContact(
                    contact = operation.contact,
                    name = operation.name,
                    address = operation.address
                )
            }
        }
        addressBookCache.update { local }
        executeRemoteAddressBookSafe(throwRemoteConsentExceptions) {
            remoteAddressBookDataSource.uploadContacts()
            Twig.info { "Address Book: executeInternalOperation - remote address book uploaded" }
        }
    }

    private suspend fun withNonCancellableSemaphore(block: suspend () -> Unit) {
        withContext(NonCancellable + Dispatchers.Default) {
            semaphore.withLock { block() }
        }
    }

    @Throws(UserRecoverableAuthException::class, UserRecoverableAuthIOException::class)
    private suspend fun <T> executeRemoteAddressBookSafe(
        throwRemoteConsentExceptions: Boolean,
        block: suspend () -> T
    ): T? {
        if (hasGoogleDrivePermission().not()) {
            return null
        }

        return try {
            block()
        } catch (e: UserRecoverableAuthException) {
            Twig.error(e) { "Address Book: remote execution failed" }
            if (throwRemoteConsentExceptions) {
                throw e
            }
            null
        } catch (e: UserRecoverableAuthIOException) {
            Twig.error(e) { "Address Book: remote execution failed" }
            if (throwRemoteConsentExceptions) {
                throw e
            }
            null
        } catch (e: GoogleAuthException) {
            Twig.error(e) { "Address Book: remote execution failed" }
            null
        } catch (e: GoogleJsonResponseException) {
            Twig.error(e) { "Address Book: remote execution failed" }
            null
        } catch (e: IOException) {
            Twig.error(e) { "Address Book: remote execution failed" }
            null
        } catch (e: IllegalArgumentException) {
            Twig.error(e) { "Address Book: remote execution failed" }
            null
        } catch (e: Exception) {
            Twig.error(e) { "Address Book: remote execution failed" }
            null
        }
    }
}

private sealed interface InternalOperation {
    data class Save(val name: String, val address: String) : InternalOperation

    data class Update(val contact: AddressBookContact, val name: String, val address: String) : InternalOperation

    data class Delete(val contact: AddressBookContact) : InternalOperation
}

private const val GOOGLE_DRIVE_SCOPE = Scopes.DRIVE_APPFOLDER

