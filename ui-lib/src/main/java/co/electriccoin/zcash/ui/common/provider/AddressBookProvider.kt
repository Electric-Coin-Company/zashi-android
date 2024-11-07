package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookEncryptor
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookSerializer
import java.io.File
import kotlin.LazyThreadSafetyMode.NONE

interface AddressBookProvider {
    fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook,
        addressBookKey: AddressBookKey
    )

    fun readAddressBookFromFile(
        file: File,
        addressBookKey: AddressBookKey
    ): AddressBook

    fun readLegacyUnencryptedAddressBookFromFile(file: File): AddressBook
}

class AddressBookProviderImpl : AddressBookProvider {
    private val addressBookSerializer by lazy(NONE) { AddressBookSerializer() }
    private val addressBookEncryptor by lazy(NONE) { AddressBookEncryptor() }

    override fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook,
        addressBookKey: AddressBookKey
    ) {
        file.outputStream().buffered().use { stream ->
            addressBookEncryptor.encryptAddressBook(
                addressBookKey,
                addressBookSerializer,
                stream,
                addressBook
            )
            stream.flush()
        }
    }

    override fun readAddressBookFromFile(
        file: File,
        addressBookKey: AddressBookKey
    ): AddressBook {
        return file.inputStream().use { stream ->
            addressBookEncryptor.decryptAddressBook(
                addressBookKey,
                addressBookSerializer,
                stream
            )
        }
    }

    override fun readLegacyUnencryptedAddressBookFromFile(file: File): AddressBook {
        return file.inputStream().use { stream ->
            addressBookSerializer.deserializeAddressBook(stream)
        }
    }
}
