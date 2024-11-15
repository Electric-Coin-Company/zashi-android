package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import java.io.File

interface AddressBookStorageProvider {
    fun getStorageFile(addressBookKey: AddressBookKey): File?

    fun getLegacyUnencryptedStorageFile(): File?

    fun getOrCreateStorageFile(addressBookKey: AddressBookKey): File

    /**
     * Create a temporary file into which data from remote is written. This file is removed after usage.
     */
    fun getOrCreateTempStorageFile(): File
}

class AddressBookStorageProviderImpl(
    private val context: Context
) : AddressBookStorageProvider {
    override fun getStorageFile(addressBookKey: AddressBookKey): File? {
        return File(context.noBackupFilesDir, addressBookKey.fileIdentifier())
            .takeIf { it.exists() && it.isFile }
    }

    override fun getLegacyUnencryptedStorageFile(): File? {
        return File(context.noBackupFilesDir, LEGACY_UNENCRYPTED_ADDRESS_BOOK_FILE_NAME)
            .takeIf { it.exists() && it.isFile }
    }

    override fun getOrCreateStorageFile(addressBookKey: AddressBookKey): File {
        return getOrCreateFile(addressBookKey.fileIdentifier())
    }

    override fun getOrCreateTempStorageFile(): File = getOrCreateFile(REMOTE_ADDRESS_BOOK_FILE_NAME_LOCAL_COPY)

    private fun getOrCreateFile(name: String): File {
        val file = File(context.noBackupFilesDir, name)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}

private const val LEGACY_UNENCRYPTED_ADDRESS_BOOK_FILE_NAME = "address_book"
private const val REMOTE_ADDRESS_BOOK_FILE_NAME_LOCAL_COPY = "address_book_temp"
