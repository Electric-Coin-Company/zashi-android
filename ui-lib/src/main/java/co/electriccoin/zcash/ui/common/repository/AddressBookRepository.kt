@file:Suppress("DEPRECATION")

package co.electriccoin.zcash.ui.common.repository

import android.content.Context
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.RemoteAddressBookDataSource
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
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

    val googleSignInRequest: Flow<Scope>

    suspend fun saveContact(name: String, address: String)

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    )

    suspend fun deleteContact(contact: AddressBookContact)

    fun onGoogleSignInSuccess(account: GoogleSignInAccount)

    fun onGoogleSignInCancelled()

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

    override val googleSignInRequest = MutableSharedFlow<Scope>()

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

    override fun onGoogleSignInSuccess(account: GoogleSignInAccount) {
        scope.launch {
            withNonCancellableSemaphore {
                internalOperation?.let {
                    Twig.info { "Address Book: onGoogleSignInSuccess" }
                    executeInternalOperation(operation = it, includeRemote = true)
                    this@AddressBookRepositoryImpl.internalOperation = null
                    internalOperationCompleted.emit(it)
                }
            }
        }
    }

    override fun onGoogleSignInCancelled() {
        scope.launch {
            withNonCancellableSemaphore {
                internalOperation?.let {
                    Twig.info { "Address Book: onGoogleSignInCancelled" }
                    executeInternalOperation(operation = it, includeRemote = false)
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
                    executeInternalOperation(operation = it, includeRemote = false)
                    this@AddressBookRepositoryImpl.internalOperation = null
                    internalOperationCompleted.emit(it)
                }
            }
        }
    }

    private suspend fun ensureSynchronization() {
        if (addressBookCache.value == null) {
            val remote = executeRemoteAddressBookSafe {
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
            executeRemoteAddressBookSafe {
                remoteAddressBookDataSource.uploadContacts()
                Twig.info { "Address Book: ensureSynchronization - remote address book uploaded" }
            }
            addressBookCache.update { merged }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mergeContacts(local: AddressBook, remote: AddressBook?): AddressBook = local // TBD

    private suspend fun withGoogleDrivePermission(internalOperation: InternalOperation) {
        if (hasGoogleDrivePermission()) {
            withNonCancellableSemaphore {
                executeInternalOperation(operation = internalOperation, includeRemote = true)
            }
        } else {
            withNonCancellableSemaphore {
                Twig.info { "Address Book: withGoogleDrivePermission - request permission" }
                this.internalOperation = internalOperation
                googleSignInRequest.emit(Scope(GOOGLE_DRIVE_SCOPE))
            }
            internalOperationCompleted.first { it == internalOperation }
        }
    }

    private suspend fun hasGoogleDrivePermission() = withContext(Dispatchers.IO) {
        GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), Scope(GOOGLE_DRIVE_SCOPE))
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun executeInternalOperation(operation: InternalOperation, includeRemote: Boolean) {
        // if (includeRemote) {
        ensureSynchronization()
        // }
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
        executeRemoteAddressBookSafe {
            remoteAddressBookDataSource.uploadContacts()
            Twig.info { "Address Book: executeInternalOperation - remote address book uploaded" }
        }
    }

    private suspend fun withNonCancellableSemaphore(block: suspend () -> Unit) {
        withContext(NonCancellable + Dispatchers.Default) {
            semaphore.withLock { block() }
        }
    }

    private suspend fun <T> executeRemoteAddressBookSafe(block: suspend () -> T): T? {
        if (hasGoogleDrivePermission().not()) {
            return null
        }

        return try {
            block()
        } catch (e: UserRecoverableAuthException) {
            Twig.error(e) { "Address Book: remote execution failed" }
            null
        } catch (e: UserRecoverableAuthIOException) {
            Twig.error(e) { "Address Book: remote execution failed" }
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

// private const val GOOGLE_DRIVE_SCOPE = Scopes.DRIVE_FILE
private const val GOOGLE_DRIVE_SCOPE = Scopes.DRIVE_APPFOLDER
