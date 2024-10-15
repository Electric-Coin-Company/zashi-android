package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import java.io.File

interface AddressBookStorageProvider {
    fun getStorageFile(): File?

    fun getOrCreateStorageFile(): File

    /**
     * Create a temporary file into which data from remote is written. This file is removed after usage.
     */
    fun getOrCreateTempStorageFile(): File
}

class AddressBookStorageProviderImpl(
    private val context: Context
) : AddressBookStorageProvider {
    override fun getStorageFile(): File? {
        return File(context.noBackupFilesDir, LOCAL_ADDRESS_BOOK_FILE_NAME)
            .takeIf { it.exists() && it.isFile }
    }

    override fun getOrCreateStorageFile(): File = getOrCreateFile(LOCAL_ADDRESS_BOOK_FILE_NAME)

    override fun getOrCreateTempStorageFile(): File = getOrCreateFile(REMOTE_ADDRESS_BOOK_FILE_NAME_LOCAL_COPY)

    private fun getOrCreateFile(name: String): File {
        val file = File(context.noBackupFilesDir, name)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}

private const val LOCAL_ADDRESS_BOOK_FILE_NAME = "address_book"
private const val REMOTE_ADDRESS_BOOK_FILE_NAME_LOCAL_COPY = "address_book_temp"
