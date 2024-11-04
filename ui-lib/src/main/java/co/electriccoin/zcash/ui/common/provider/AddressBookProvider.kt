package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookV1Serializer
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
}

class AddressBookProviderImpl : AddressBookProvider {
    private val addressBookV1Serializer by lazy(NONE) { AddressBookV1Serializer() }

    override fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook,
        addressBookKey: AddressBookKey
    ) {
        file.outputStream().buffered().use { stream ->
            addressBookV1Serializer.serializeAddressBook(stream, addressBook)
            stream.flush()
        }
    }

    override fun readAddressBookFromFile(
        file: File,
        addressBookKey: AddressBookKey
    ): AddressBook {
        return file.inputStream().use { stream ->
            addressBookV1Serializer.deserializeAddressBook(stream)
        }
    }
}

