package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import java.io.File

interface AddressBookStorageProvider {
    fun getStorageFile(addressBookKey: AddressBookKey): File?

    fun getLegacyUnencryptedStorageFile(): File?

    fun getOrCreateStorageFile(addressBookKey: AddressBookKey): File

    // /**
    //  * Create a temporary file into which data from remote is written. This file is removed after usage.
    //  */
    // fun getOrCreateTempStorageFile(): File
}

class AddressBookStorageProviderImpl(
    private val context: Context
) : AddressBookStorageProvider {
    override fun getStorageFile(addressBookKey: AddressBookKey): File? {
        return File(getOrCreateAddressBookDir(), addressBookKey.fileIdentifier())
            .takeIf { it.exists() && it.isFile }
    }

    override fun getLegacyUnencryptedStorageFile(): File? {
        return File(context.noBackupFilesDir, LEGACY_UNENCRYPTED_ADDRESS_BOOK_FILE_NAME)
            .takeIf { it.exists() && it.isFile }
    }

    override fun getOrCreateStorageFile(addressBookKey: AddressBookKey): File {
        val file = File(getOrCreateAddressBookDir(), addressBookKey.fileIdentifier())
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    // override fun getOrCreateTempStorageFile(): File {
    //     val file = File(context.noBackupFilesDir, REMOTE_ADDRESS_BOOK_FILE_NAME_LOCAL_COPY)
    //     if (!file.exists()) {
    //         file.createNewFile()
    //     }
    //     return file
    // }

    private fun getOrCreateAddressBookDir(): File {
        val filesDir = context.filesDir
        val addressBookDir = File(filesDir, "address_book")
        if (!addressBookDir.exists()) {
            addressBookDir.mkdir()
        }
        return addressBookDir
    }
}

private const val LEGACY_UNENCRYPTED_ADDRESS_BOOK_FILE_NAME = "address_book"
// private const val REMOTE_ADDRESS_BOOK_FILE_NAME_LOCAL_COPY = "address_book_temp"
