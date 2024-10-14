@file:Suppress("DEPRECATION")

package co.electriccoin.zcash.ui.common.datasource

import android.content.Context
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import com.google.api.services.drive.model.File as GoogleDriveFile

interface RemoteAddressBookDataSource {

    @Throws(
        UserRecoverableAuthException::class,
        UserRecoverableAuthIOException::class,
        IOException::class,
        IllegalArgumentException::class,
        GoogleJsonResponseException::class,
    )
    suspend fun fetchContacts(): AddressBook?

    @Throws(
        UserRecoverableAuthException::class,
        UserRecoverableAuthIOException::class,
        IOException::class,
        IllegalArgumentException::class,
        GoogleJsonResponseException::class,
    )
    suspend fun uploadContacts()
}

class RemoteAddressBookDataSourceImpl(
    private val context: Context,
    private val addressBookStorageProvider: AddressBookStorageProvider,
    private val addressBookProvider: AddressBookProvider,
) : RemoteAddressBookDataSource {
    override suspend fun fetchContacts(): AddressBook? = withContext(Dispatchers.IO) {

        fun fetchRemoteFile(service: Drive): GoogleDriveFile? {
            return try {
                service.files().list().setSpaces("appDataFolder").execute().files
                    .find { it.name == REMOTE_ADDRESS_BOOK_FILE_NAME }
            } catch (e: GoogleJsonResponseException) {
                Twig.info(e) { "No files found on google drive name $REMOTE_ADDRESS_BOOK_FILE_NAME" }
                null
            }
        }

        fun downloadRemoteFile(service: Drive, file: GoogleDriveFile): File? {
            return try {
                val localFile = addressBookStorageProvider.getOrCreateTempStorageFile()

                localFile.outputStream().use { outputStream ->
                    service.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                }

                localFile
            } catch (e: GoogleJsonResponseException) {
                Twig.info(e) { "No files found on google drive name $REMOTE_ADDRESS_BOOK_FILE_NAME" }
                null
            }
        }

        var localTempFile: File? = null

        return@withContext try {
            val drive = createGoogleDriveService()
            val remoteFile = fetchRemoteFile(drive)

            if (remoteFile == null) {
                Twig.info { "No address book file found to upload" }
                return@withContext null
            }

            localTempFile = downloadRemoteFile(drive, remoteFile) ?: return@withContext null

            addressBookProvider.readAddressBookFromFile(localTempFile)
        } finally {
            localTempFile?.delete()
        }
    }

    override suspend fun uploadContacts() = withContext(Dispatchers.IO) {

        fun deleteExistingRemoteFiles(service: Drive) {
            try {
                val files = service.files().list().setSpaces("appDataFolder").execute().files
                    .filter { it.name == REMOTE_ADDRESS_BOOK_FILE_NAME }
                files.forEach {
                    service.files().delete(it.id).execute()
                }
            } catch (e: GoogleJsonResponseException) {
                if (e.statusCode == 404) {
                    Twig.info(e) { "No files found on google drive name $REMOTE_ADDRESS_BOOK_FILE_NAME" }
                } else {
                    throw e
                }
            }
        }

        fun createRemoteFile(file: File, service: Drive) {
            val metadata = GoogleDriveFile()
                .setParents(listOf("appDataFolder"))
                .setMimeType("application/octet-stream")
                .setName(file.name)
            val fileContent = FileContent("application/octet-stream", file)

            service.files().create(metadata, fileContent).execute()
        }

        val drive = createGoogleDriveService()
        val localFile = addressBookStorageProvider.getStorageFile()

        if (localFile == null) {
            Twig.info { "No address book file found to upload" }
            return@withContext
        }

        deleteExistingRemoteFiles(drive)
        createRemoteFile(localFile, drive)
    }

    private fun createGoogleDriveService(): Drive {
        val account = GoogleSignIn.getLastSignedInAccount(context)

        val credentials = GoogleAccountCredential.usingOAuth2(context, listOf(Scopes.DRIVE_FILE))
            .apply {
                selectedAccount = account?.account ?: allAccounts.firstOrNull()
            }

        return Drive
            .Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credentials
            )
            .setApplicationName(if (BuildConfig.DEBUG) "secant-android-debug" else "secant-android-release")
            .build()
    }
}

private const val REMOTE_ADDRESS_BOOK_FILE_NAME = "address_book"
