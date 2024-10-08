package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

interface LocalAddressBookStorageProvider {
    fun openStorageInputStream(): FileInputStream?

    fun openStorageOutputStream(): FileOutputStream?
}

class LocalAddressBookStorageProviderImpl(
    private val context: Context
) : LocalAddressBookStorageProvider {
    override fun openStorageInputStream(): FileInputStream? {
        return try {
            context.openFileInput(LOCAL_ADDRESS_BOOK_FILE_NAME)
        } catch (e: FileNotFoundException) {
            null
        } catch (e: IOException) {
            null
        }
    }

    override fun openStorageOutputStream(): FileOutputStream? {
        return try {
            context.openFileOutput(LOCAL_ADDRESS_BOOK_FILE_NAME, Context.MODE_PRIVATE)
        } catch (e: Exception) {
            null
        } catch (e: Exception) {
            null
        }
    }
}

private const val LOCAL_ADDRESS_BOOK_FILE_NAME = "address_book"
