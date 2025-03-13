package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookEncryptor
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookSerializer
import java.io.File

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

class AddressBookProviderImpl(
    private val addressBookEncryptor: AddressBookEncryptor,
    private val addressBookSerializer: AddressBookSerializer,
) : AddressBookProvider {
    override fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook,
        addressBookKey: AddressBookKey
    ) {
        file.outputStream().buffered().use { stream ->
            addressBookEncryptor.encrypt(
                key = addressBookKey,
                outputStream = stream,
                data = addressBook
            )
            stream.flush()
        }
    }

    override fun readAddressBookFromFile(
        file: File,
        addressBookKey: AddressBookKey
    ): AddressBook =
        file.inputStream().use { stream ->
            addressBookEncryptor.decrypt(
                key = addressBookKey,
                inputStream = stream
            )
        }

    override fun readLegacyUnencryptedAddressBookFromFile(file: File): AddressBook =
        file.inputStream().use { stream ->
            addressBookSerializer.deserializeAddressBook(stream)
        }
}
