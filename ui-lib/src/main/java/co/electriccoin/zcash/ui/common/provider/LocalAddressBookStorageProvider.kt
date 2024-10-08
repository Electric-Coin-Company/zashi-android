package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import co.electriccoin.zcash.spackle.Twig
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
            Twig.error(e) { "Address Book file does not exist yet" }
            null
        } catch (e: IOException) {
            Twig.error(e) { "Error reading from Address Book file" }
            null
        }
    }

    override fun openStorageOutputStream(): FileOutputStream? {
        return try {
            context.openFileOutput(LOCAL_ADDRESS_BOOK_FILE_NAME, Context.MODE_PRIVATE)
        } catch (e: FileNotFoundException) {
            Twig.error(e) { "Address Book file does not exist yet" }
            null
        } catch (e: IOException) {
            Twig.error(e) { "Error writing to Address Book file" }
            null
        }
    }
}

private const val LOCAL_ADDRESS_BOOK_FILE_NAME = "address_book"
